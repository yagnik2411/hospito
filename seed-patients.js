import http from 'k6/http';

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

const BLOOD_GROUPS = [
  'A_POSITIVE', 'A_NEGATIVE',
  'B_POSITIVE', 'B_NEGATIVE',
  'O_POSITIVE', 'O_NEGATIVE',
  'AB_POSITIVE', 'AB_NEGATIVE',
];

const BRANCH_IDS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];

export default function () {

  console.log('🤒 Creating 5000 patients...');
  let successCount = 0;
  let failCount = 0;

  for (let p = 0; p < 5000; p++) {
    const email = `patient${p + 1}@hospito.com`;

    const res = http.post(
      `${BASE_URL}/patients`,
      JSON.stringify({
        name: `Patient ${p + 1}`,
        email: email,
        password: 'patient123',
        phone: `+91-${String(9000000000 + p)}`,
        dateOfBirth: '1990-01-01',
        gender: p % 2 === 0 ? 'MALE' : 'FEMALE',
        address: `${p + 1} Patient Street`,
        bloodGroup: BLOOD_GROUPS[p % BLOOD_GROUPS.length],
        branchId: BRANCH_IDS[p % BRANCH_IDS.length],
      }),
      { headers: JSON_HEADERS }
    );

    if (res.status === 200 || res.status === 201) {
      const body = JSON.parse(res.body);
      if (body.success) {
        successCount++;
        if (successCount % 500 === 0) {
          console.log(`✅ ${successCount} patients created...`);
        }
      } else {
        failCount++;
        if (failCount <= 3) {
          console.log(`❌ Failed: ${body.message}`);
        }
      }
    } else {
      failCount++;
      if (failCount <= 3) {
        console.log(`❌ HTTP ${res.status}: ${res.body.substring(0, 100)}`);
      }
    }
  }

  console.log('\n═══════════════════════════════════════');
  console.log('✅ PATIENT SEED COMPLETE');
  console.log(`   Success: ${successCount}`);
  console.log(`   Failed:  ${failCount}`);
  console.log('═══════════════════════════════════════');
}
