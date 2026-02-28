import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

// ── CUSTOM METRICS ────────────────────────────────────────────────────────────
const errorRate        = new Rate('error_rate');
const cachedTrend      = new Trend('cached_endpoint_ms');
const dbTrend          = new Trend('db_endpoint_ms');
const appointmentsMade = new Counter('appointments_booked');
const billsPaid        = new Counter('bills_paid');

// ── TEST CONFIG ───────────────────────────────────────────────────────────────
export const options = {
  scenarios: {

    // 1 SUPER ADMIN — views everything
    super_admin: {
      executor: 'constant-vus',
      vus: 1,
      duration: '90s',
      exec: 'superAdminFlow',
      tags: { role: 'super_admin' },
    },

    // 10 BRANCH ADMINS — manage branches
    branch_admins: {
      executor: 'constant-vus',
      vus: 10,
      duration: '90s',
      exec: 'branchAdminFlow',
      tags: { role: 'branch_admin' },
    },

    // 300 DOCTORS — view appointments
    doctors: {
      executor: 'constant-vus',
      vus: 300,
      duration: '90s',
      exec: 'doctorFlow',
      tags: { role: 'doctor' },
    },

    // 5000 PATIENTS — full lifecycle
    patients: {
      executor: 'ramping-vus',
      startVUs: 100,
      stages: [
        { duration: '20s', target: 1000 },
        { duration: '40s', target: 5000 },
        { duration: '20s', target: 1000 },
        { duration: '10s', target: 0    },
      ],
      exec: 'patientFlow',
      tags: { role: 'patient' },
    },
  },

  thresholds: {
    'http_req_duration':              ['p(95)<2000'],  // 2s max under stress
    'http_req_duration{role:patient}':['p(95)<3000'],  // patients allowed 3s
    'error_rate':                     ['rate<0.10'],   // under 10% errors
    'cached_endpoint_ms':             ['p(95)<500'],   // cached under 500ms
  },
};

const BASE_URL = 'http://localhost:8080/api/v1';

// ── SHARED DATA ───────────────────────────────────────────────────────────────
const DOCTOR_IDS   = Array.from({ length: 300 }, (_, i) => i + 1);
const BRANCH_IDS   = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
const PATIENT_IDS  = Array.from({ length: 5000 }, (_, i) => i + 1);

// Appointment times — spread across next 30 days to avoid conflicts
function randomFutureTime(vuId, iteration) {
  const base = new Date('2026-03-10T08:00:00');
  base.setDate(base.getDate() + (vuId % 30));
  base.setHours(8 + (iteration % 10));
  base.setMinutes((vuId % 4) * 15);
  return base.toISOString().slice(0, 19);
}

function authHeaders(token) {
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`,
  };
}

function login(email, password) {
  const res = http.post(
    `${BASE_URL}/auth/login`,
    JSON.stringify({ email, password }),
    { headers: { 'Content-Type': 'application/json' } }
  );
  if (res.status === 200) {
    try { return JSON.parse(res.body).data.token; }
    catch (e) { return null; }
  }
  return null;
}

// ── SCENARIO 1: SUPER ADMIN ───────────────────────────────────────────────────
export function superAdminFlow() {
  const token = login('yagnik@hospito.com', 'admin123');
  if (!token) { sleep(2); return; }

  const headers = authHeaders(token);

  // View all branches
  const branches = http.get(`${BASE_URL}/branches`, { headers });
  check(branches, { 'SA: GET branches 200': (r) => r.status === 200 });
  cachedTrend.add(branches.timings.duration);
  errorRate.add(branches.status !== 200);
  sleep(1);

  // View each branch
  BRANCH_IDS.slice(0, 3).forEach(id => {
    const b = http.get(`${BASE_URL}/branches/${id}`, { headers });
    check(b, { 'SA: GET branch by id 200': (r) => r.status === 200 });
    cachedTrend.add(b.timings.duration);
    errorRate.add(b.status !== 200);
    sleep(0.5);
  });

  // View doctors
  const doctors = http.get(`${BASE_URL}/doctors?branchId=1`, { headers });
  check(doctors, { 'SA: GET doctors 200': (r) => r.status === 200 });
  dbTrend.add(doctors.timings.duration);
  errorRate.add(doctors.status !== 200);

  sleep(3);
}

// ── SCENARIO 2: BRANCH ADMIN ──────────────────────────────────────────────────
export function branchAdminFlow() {
  const vuId = __VU;
  const adminNum = (vuId % 10) + 1;
  const token = login(`branchadmin${adminNum}@hospito.com`, 'branch123');
  if (!token) { sleep(2); return; }

  const headers = authHeaders(token);
  const branchId = BRANCH_IDS[adminNum % BRANCH_IDS.length];

  // View their branch
  const branch = http.get(`${BASE_URL}/branches/${branchId}`, { headers });
  check(branch, { 'BA: GET branch 200': (r) => r.status === 200 });
  cachedTrend.add(branch.timings.duration);
  errorRate.add(branch.status !== 200);
  sleep(1);

  // View doctors in branch
  const doctors = http.get(`${BASE_URL}/doctors?branchId=${branchId}`, { headers });
  check(doctors, { 'BA: GET doctors 200': (r) => r.status === 200 });
  dbTrend.add(doctors.timings.duration);
  errorRate.add(doctors.status !== 200);
  sleep(1);

  // View appointments in branch
  const appts = http.get(`${BASE_URL}/appointments/branch/${branchId}`, { headers });
  check(appts, { 'BA: GET appointments 200': (r) => r.status === 200 });
  dbTrend.add(appts.timings.duration);
  errorRate.add(appts.status !== 200);

  sleep(2);
}

// ── SCENARIO 3: DOCTOR ────────────────────────────────────────────────────────
export function doctorFlow() {
  const vuId = __VU;
  const doctorNum = (vuId % 300) + 1;
  const token = login(`doctor${doctorNum}@hospito.com`, 'doctor123');
  if (!token) { sleep(2); return; }

  const headers = authHeaders(token);
  const doctorId = DOCTOR_IDS[doctorNum - 1];
  const branchId = BRANCH_IDS[doctorNum % BRANCH_IDS.length];

  // View own profile
  const profile = http.get(`${BASE_URL}/doctors/${doctorId}`, { headers });
  check(profile, { 'DR: GET doctor profile 200': (r) => r.status === 200 });
  cachedTrend.add(profile.timings.duration);
  errorRate.add(profile.status !== 200);
  sleep(1);

  // View appointments in their branch
  const appts = http.get(`${BASE_URL}/appointments/branch/${branchId}`, { headers });
  check(appts, { 'DR: GET appointments 200': (r) => r.status === 200 });
  dbTrend.add(appts.timings.duration);
  errorRate.add(appts.status !== 200);

  sleep(3);
}

// ── SCENARIO 4: PATIENT — FULL LIFECYCLE ──────────────────────────────────────
export function patientFlow() {
  const vuId  = __VU;
  const iter  = __ITER;
  const patientNum = ((vuId + iter) % 5000) + 1;
  const token = login(`patient${patientNum}@hospito.com`, 'patient123');
  if (!token) {
    errorRate.add(1);
    sleep(1);
    return;
  }

  const headers  = authHeaders(token);
  const doctorId = DOCTOR_IDS[(vuId + iter) % DOCTOR_IDS.length];
  const branchId = 1;

  // Step 1 — View available doctors
  const doctors = http.get(`${BASE_URL}/doctors?branchId=${branchId}`, { headers });
  check(doctors, { 'PT: GET doctors 200': (r) => r.status === 200 });
  errorRate.add(doctors.status !== 200);
  sleep(0.5);

  // Step 2 — Book appointment
  const apptTime = randomFutureTime(vuId, iter);
  const apptRes = http.post(
    `${BASE_URL}/appointments`,
    JSON.stringify({
      patientId:       patientNum,
      doctorId:        doctorId,
      branchId:        branchId,
      appointmentTime: apptTime,
      reason:          'Routine checkup',
    }),
    { headers }
  );

  const apptOk = check(apptRes, {
    'PT: Book appointment 200': (r) => r.status === 200,
    'PT: Appointment success':  (r) => {
      try { return JSON.parse(r.body).success === true; }
      catch(e) { return false; }
    },
  });
  errorRate.add(apptRes.status !== 200);

  if (!apptOk) { sleep(1); return; }

  let appointmentId;
  try {
    appointmentId = JSON.parse(apptRes.body).data.id;
    appointmentsMade.add(1);
  } catch(e) { sleep(1); return; }

  sleep(0.5);

  // Step 3 — View appointment
  const viewAppt = http.get(`${BASE_URL}/appointments/${appointmentId}`, { headers });
  check(viewAppt, { 'PT: GET appointment 200': (r) => r.status === 200 });
  errorRate.add(viewAppt.status !== 200);
  sleep(0.5);

  // Step 4 — Admin confirms appointment (using admin token for status update)
  const adminToken = __ENV.TOKEN;
  const confirmRes = http.put(
    `${BASE_URL}/appointments/${appointmentId}/status`,
    JSON.stringify({ status: 'CONFIRMED', notes: 'Confirmed by system' }),
    { headers: authHeaders(adminToken) }
  );
  check(confirmRes, { 'PT: Confirm appointment 200': (r) => r.status === 200 });
  sleep(0.3);

  // Step 5 — Mark IN_PROGRESS
  http.put(
    `${BASE_URL}/appointments/${appointmentId}/status`,
    JSON.stringify({ status: 'IN_PROGRESS', notes: 'Doctor attending' }),
    { headers: authHeaders(adminToken) }
  );
  sleep(0.3);

  // Step 6 — Mark COMPLETED
  http.put(
    `${BASE_URL}/appointments/${appointmentId}/status`,
    JSON.stringify({ status: 'COMPLETED', notes: 'Appointment done' }),
    { headers: authHeaders(adminToken) }
  );
  sleep(0.3);

  // Step 7 — Create bill
  const billRes = http.post(
    `${BASE_URL}/bills`,
    JSON.stringify({
      appointmentId: appointmentId,
      items: [
        {
          description: 'Consultation Fee',
          quantity: 1,
          unitPrice: 500.00,
        },
      ],
    }),
    { headers: authHeaders(adminToken) }
  );

  const billOk = check(billRes, {
    'PT: Create bill 200': (r) => r.status === 200,
  });
  errorRate.add(billRes.status !== 200);

  if (!billOk) { sleep(1); return; }

  let billId;
  try {
    billId = JSON.parse(billRes.body).data.id;
  } catch(e) { sleep(1); return; }

  sleep(0.3);

  // Step 8 — Pay bill (CASH)
  const payRes = http.post(
    `${BASE_URL}/bills/${billId}/pay`,
    JSON.stringify({ paymentMethod: 'CASH' }),
    { headers: authHeaders(adminToken) }
  );

  check(payRes, { 'PT: Pay bill 200': (r) => r.status === 200 });
  errorRate.add(payRes.status !== 200);
  if (payRes.status === 200) billsPaid.add(1);

  sleep(1);
}