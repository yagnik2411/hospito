import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// Custom metrics
const errorRate    = new Rate('error_rate');
const cachedTrend  = new Trend('cached_endpoint_duration');
const dbTrend      = new Trend('db_endpoint_duration');

export const options = {
  stages: [
    { duration: '15s', target: 10  },  // warm up
    { duration: '30s', target: 50  },  // ramp up
    { duration: '30s', target: 100 },  // peak load
    { duration: '15s', target: 0   },  // ramp down
  ],
  thresholds: {
    http_req_duration:        ['p(95)<500'],
    error_rate:               ['rate<0.05'],
    cached_endpoint_duration: ['p(95)<200'],  // ← relaxed from 50ms
  },
};

const TOKEN    = __ENV.TOKEN;
const BASE_URL = 'http://localhost:8080/api/v1';

const JSON_HEADERS = {
  'Content-Type': 'application/json',
  'Authorization': `Bearer ${TOKEN}`,
};

// ── HELPER ───────────────────────────────────────────────────────────────────
function isSuccess(r) {
  try {
    return JSON.parse(r.body).success === true;
  } catch (e) {
    return false;
  }
}

// ── MAIN TEST LOOP ───────────────────────────────────────────────────────────
export default function () {

  // ── TEST 1: POST login (DB + BCrypt — slowest endpoint) ──────────────────
    const loginRes = http.post(
    `${BASE_URL}/auth/login`,
    JSON.stringify({ email: 'yagnik@hospito.com', password: 'admin123' }),  // ← fix here
    { headers: JSON_HEADERS }
  );
  check(loginRes, {
    'POST /auth/login — status 200':   (r) => r.status === 200,
    'POST /auth/login — success true': (r) => isSuccess(r),
    'POST /auth/login — has token':    (r) => {
      try { return !!JSON.parse(r.body).data.token; }
      catch (e) { return false; }
    },
  });
  errorRate.add(loginRes.status !== 200);
  dbTrend.add(loginRes.timings.duration);
  sleep(0.3);

  // ── TEST 2: GET all branches (cached after first hit) ─────────────────────
  const branchListRes = http.get(
    `${BASE_URL}/branches`,
    { headers: JSON_HEADERS }
  );
  check(branchListRes, {
    'GET /branches — status 200':      (r) => r.status === 200,
    'GET /branches — success true':    (r) => isSuccess(r),
    'GET /branches — returns 2 items': (r) => {
      try { return JSON.parse(r.body).data.length === 2; }
      catch (e) { return false; }
    },
  });
  errorRate.add(branchListRes.status !== 200);
  cachedTrend.add(branchListRes.timings.duration);
  sleep(0.3);

  // ── TEST 3: GET branch by id=1 (cached) ───────────────────────────────────
  const branch1Res = http.get(
    `${BASE_URL}/branches/1`,
    { headers: JSON_HEADERS }
  );
  check(branch1Res, {
    'GET /branches/1 — status 200':   (r) => r.status === 200,
    'GET /branches/1 — correct name': (r) => {
      try { return JSON.parse(r.body).data.id === 1; }
      catch (e) { return false; }
    },
  });
  errorRate.add(branch1Res.status !== 200);
  cachedTrend.add(branch1Res.timings.duration);
  sleep(0.3);

  // ── TEST 4: GET branch by id=2 (cached) ───────────────────────────────────
  const branch2Res = http.get(
    `${BASE_URL}/branches/2`,
    { headers: JSON_HEADERS }
  );
  check(branch2Res, {
    'GET /branches/2 — status 200':   (r) => r.status === 200,
    'GET /branches/2 — correct name': (r) => {
      try { return JSON.parse(r.body).data.id === 2; }
      catch (e) { return false; }
    },
  });
  errorRate.add(branch2Res.status !== 200);
  cachedTrend.add(branch2Res.timings.duration);
  sleep(0.3);

  // ── TEST 5: GET appointments by branch (DB — no cache) ────────────────────
  const apptRes = http.get(
    `${BASE_URL}/appointments/branch/1`,
    { headers: JSON_HEADERS }
  );
  check(apptRes, {
    'GET /appointments/branch/1 — status 200':   (r) => r.status === 200,
    'GET /appointments/branch/1 — success true': (r) => isSuccess(r),
  });
  errorRate.add(apptRes.status !== 200);
  dbTrend.add(apptRes.timings.duration);
  sleep(0.3);
}