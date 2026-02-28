import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 1,
    iterations: 1,
    thresholds: {},
};

const BASE_URL = 'http://localhost:8080/api/v1';
const ADMIN_TOKEN = __ENV.TOKEN;

const JSON_HEADERS = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${ADMIN_TOKEN}`,
};

// ── CITIES FOR BRANCH NAMES ──────────────────────────────────────────────────
const CITIES = [
    { name: 'Ahmedabad', state: 'Gujarat' },
    { name: 'Pune', state: 'Maharashtra' },
    { name: 'Bangalore', state: 'Karnataka' },
    { name: 'Chennai', state: 'Tamil Nadu' },
    { name: 'Hyderabad', state: 'Telangana' },
    { name: 'Kolkata', state: 'West Bengal' },
    { name: 'Jaipur', state: 'Rajasthan' },
    { name: 'Lucknow', state: 'Uttar Pradesh' },
    { name: 'Bhopal', state: 'Madhya Pradesh' },
    { name: 'Patna', state: 'Bihar' },
];

const SPECIALIZATIONS = [
    'Cardiologist', 'Neurologist', 'Orthopedist',
    'Dermatologist', 'Pediatrician', 'General Physician',
    'Ophthalmologist', 'ENT Specialist', 'Psychiatrist', 'Oncologist',
];

// ── SETUP: runs once, single VU ──────────────────────────────────────────────
export default function () {

    console.log('🏥 Starting seed...');

    // ── STEP 1: Create 10 branches ────────────────────────────────────────────
    console.log('🏢 Creating 10 branches...');
    const branchIds = [1, 2]; // already created by setup.sh

    for (let i = 0; i < CITIES.length; i++) {
        const city = CITIES[i];
        const res = http.post(
            `${BASE_URL}/branches`,
            JSON.stringify({
                name: `Hospito ${city.name}`,
                address: `${i + 1}00 Main Road, ${city.name}`,
                city: city.name,
                state: city.state,
                contactPhone: `+91-98765432${i + 10}`,
                email: `${city.name.toLowerCase()}@hospito.com`,
            }),
            { headers: JSON_HEADERS }
        );

        if (res.status === 200 || res.status === 201) {
            const body = JSON.parse(res.body);
            if (body.success) {
                branchIds.push(body.data.id);
                console.log(`✅ Branch created: ${city.name} (id=${body.data.id})`);
            }
        } else {
            console.log(`⚠️ Branch failed: ${city.name} — ${res.status}`);
        }
    }

    console.log(`\n📊 Total branches: ${branchIds.length}`);

    // ── STEP 2: Create 10 branch admins ──────────────────────────────────────
    console.log('\n👔 Creating 10 branch admins...');
    const branchAdminTokens = [];

    for (let i = 0; i < 10; i++) {
        const email = `branchadmin${i + 1}@hospito.com`;
        const password = 'branch123';

        const res = http.post(
            `${BASE_URL}/auth/register`,
            JSON.stringify({
                name: `Branch Admin ${i + 1}`,
                email: email,
                password: password,
                role: 'BRANCH_ADMIN',
            }),
            { headers: { 'Content-Type': 'application/json' } }
        );

        if (res.status === 200 || res.status === 201) {
            const body = JSON.parse(res.body);
            if (body.success) {
                branchAdminTokens.push({ token: body.data.token, email, password });
                console.log(`✅ Branch Admin ${i + 1} created`);
            }
        }
    }

    // ── STEP 3: Create 300 doctors (30 per branch, using first 10 branches) ───
    console.log('\n👨‍⚕️ Creating 300 doctors...');
    const doctorIds = [];
    let doctorCount = 0;

    for (let b = 0; b < 10; b++) {
        const branchId = branchIds[b] || 1;

        for (let d = 0; d < 30; d++) {
            doctorCount++;
            const spec = SPECIALIZATIONS[d % SPECIALIZATIONS.length];
            const licenseNum = `MED-${2024}-${String(doctorCount).padStart(4, '0')}`;
            const email = `doctor${doctorCount}@hospito.com`;

            const res = http.post(
                `${BASE_URL}/doctors`,
                JSON.stringify({
                    name: `Dr. Doctor ${doctorCount}`,
                    specialization: spec,
                    licenseNumber: licenseNum,
                    bio: `${spec} with 5 years experience`,
                    email: email,
                    password: 'doctor123',
                    branchId: branchId,
                }),
                { headers: JSON_HEADERS }
            );

            if (res.status === 200 || res.status === 201) {
                const body = JSON.parse(res.body);
                if (body.success) {
                    doctorIds.push({ id: body.data.id, branchId: branchId });
                    if (doctorCount % 30 === 0) {
                        console.log(`✅ ${doctorCount} doctors created...`);
                    }
                }
            }
        }
    }

    console.log(`\n📊 Total doctors created: ${doctorIds.length}`);

    // ── STEP 4: Create 5000 patients ─────────────────────────────────────────
    console.log('\n🤒 Creating 5000 patients...');
    const patientIds = [];

    for (let p = 0; p < 5000; p++) {
        const email = `patient${p + 1}@hospito.com`;

        const res = http.post(
  `${BASE_URL}/patients`,
  JSON.stringify({
    name: `Patient ${p + 1}`,
    email: email,
    password: 'patient123',                              // ← add this
    phone: `+91-${String(9000000000 + p)}`,
    dateOfBirth: '1990-01-01',
    gender: p % 2 === 0 ? 'MALE' : 'FEMALE',
    address: `${p + 1} Patient Street, Surat`,
    bloodGroup: BLOOD_GROUPS[p % BLOOD_GROUPS.length],
    branchId: branchIds[p % branchIds.length] || 1,
  }),
  { headers: JSON_HEADERS }
);

        if (res.status === 200 || res.status === 201) {
            const body = JSON.parse(res.body);
            if (body.success) {
                patientIds.push(body.data.id);
                if ((p + 1) % 500 === 0) {
                    console.log(`✅ ${p + 1} patients created...`);
                }
            }
        }
    }

    console.log(`\n📊 Total patients created: ${patientIds.length}`);

    // ── SUMMARY ───────────────────────────────────────────────────────────────
    console.log('\n═══════════════════════════════════════');
    console.log('✅ SEED COMPLETE');
    console.log(`   Branches:      ${branchIds.length}`);
    console.log(`   Branch Admins: ${branchAdminTokens.length}`);
    console.log(`   Doctors:       ${doctorIds.length}`);
    console.log(`   Patients:      ${patientIds.length}`);
    console.log('═══════════════════════════════════════');
    console.log('\nSave these for load test:');
    console.log(`DOCTOR_IDS=${JSON.stringify(doctorIds.slice(0, 10).map(d => d.id))}`);
    console.log(`BRANCH_IDS=${JSON.stringify(branchIds.slice(0, 10))}`);
    console.log(`PATIENT_COUNT=${patientIds.length}`);
}