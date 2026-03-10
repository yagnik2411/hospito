import { useState, useRef, useMemo } from 'react'
import EndpointCard, { CodeBlock } from '../components/EndpointCard'
import styles from './Docs.module.css'

const SWAGGER = 'https://hospito-production.up.railway.app/swagger-ui/index.html'

const GROUPS = [
  { id: 'intro',       label: 'Overview',     sidebar: 'Introduction' },
  { id: 'auth',        label: 'Auth',          sidebar: 'Authentication' },
  { id: 'chain',       label: 'Chain',         sidebar: 'Hospital Chain' },
  { id: 'branch',      label: 'Branch',        sidebar: 'Branches' },
  { id: 'doctor',      label: 'Doctor',        sidebar: 'Doctors' },
  { id: 'patient',     label: 'Patient',       sidebar: 'Patients' },
  { id: 'appointment', label: 'Appointment',   sidebar: 'Appointments' },
  { id: 'billing',     label: 'Billing',       sidebar: 'Billing' },
]

// Flat searchable index of all endpoints
const ALL_ENDPOINTS = [
  { group: 'auth',        method: 'POST',  path: '/api/v1/auth/register',              summary: 'Create a new user account',        tags: 'register user auth public' },
  { group: 'auth',        method: 'POST',  path: '/api/v1/auth/login',                 summary: 'Authenticate and receive JWT',      tags: 'login jwt token public' },
  { group: 'chain',       method: 'POST',  path: '/api/v1/chains',                     summary: 'Create hospital chain',             tags: 'chain create super_admin' },
  { group: 'chain',       method: 'GET',   path: '/api/v1/chains',                     summary: 'Get chain details',                 tags: 'chain get' },
  { group: 'chain',       method: 'PUT',   path: '/api/v1/chains/{id}',                summary: 'Update chain details',              tags: 'chain update' },
  { group: 'branch',      method: 'POST',  path: '/api/v1/branches',                   summary: 'Create a branch',                   tags: 'branch create' },
  { group: 'branch',      method: 'GET',   path: '/api/v1/branches',                   summary: 'List all branches',                 tags: 'branch list get' },
  { group: 'branch',      method: 'DELETE',path: '/api/v1/branches/{id}',              summary: 'Soft-delete a branch',              tags: 'branch delete' },
  { group: 'doctor',      method: 'POST',  path: '/api/v1/doctors',                    summary: 'Register a doctor',                 tags: 'doctor register create' },
  { group: 'doctor',      method: 'PUT',   path: '/api/v1/doctors/{id}/availability',  summary: 'Set weekly availability',           tags: 'doctor availability schedule' },
  { group: 'doctor',      method: 'PATCH', path: '/api/v1/doctors/{id}/transfer',      summary: 'Transfer to another branch',        tags: 'doctor transfer' },
  { group: 'patient',     method: 'POST',  path: '/api/v1/patients/register',          summary: 'Register a patient',               tags: 'patient register public' },
  { group: 'patient',     method: 'POST',  path: '/api/v1/patients/{id}/insurance',    summary: 'Add insurance policy',              tags: 'patient insurance' },
  { group: 'patient',     method: 'GET',   path: '/api/v1/patients/{id}/medical-records', summary: 'Get medical records',           tags: 'patient medical records' },
  { group: 'patient',     method: 'POST',  path: '/api/v1/patients/{id}/medical-records', summary: 'Add medical record',            tags: 'patient medical records doctor' },
  { group: 'appointment', method: 'POST',  path: '/api/v1/appointments',               summary: 'Book an appointment',              tags: 'appointment book create kafka' },
  { group: 'appointment', method: 'PATCH', path: '/api/v1/appointments/{id}/status',   summary: 'Update appointment status',        tags: 'appointment status state machine' },
  { group: 'appointment', method: 'GET',   path: '/api/v1/appointments',               summary: 'List appointments',                tags: 'appointment list get' },
  { group: 'billing',     method: 'POST',  path: '/api/v1/bills',                      summary: 'Create a bill',                    tags: 'billing bill create' },
  { group: 'billing',     method: 'POST',  path: '/api/v1/bills/{id}/pay',             summary: 'Process payment',                  tags: 'billing payment pay cash card upi insurance kafka' },
  { group: 'billing',     method: 'PATCH', path: '/api/v1/bills/{id}/waive',           summary: 'Waive a bill',                     tags: 'billing waive' },
]

const METHOD_COLOR = { GET:'#4D9EFF', POST:'#00FFB2', PUT:'#FFD166', PATCH:'#A78BFA', DELETE:'#FF4D6A' }

export default function Docs() {
  const [active, setActive] = useState('intro')
  const [search, setSearch] = useState('')
  const refs = useRef({})

  const searchResults = useMemo(() => {
    if (!search.trim()) return []
    const q = search.toLowerCase()
    return ALL_ENDPOINTS.filter(ep =>
      ep.path.toLowerCase().includes(q) ||
      ep.summary.toLowerCase().includes(q) ||
      ep.method.toLowerCase().includes(q) ||
      ep.tags.toLowerCase().includes(q)
    )
  }, [search])

  const scrollTo = (id) => {
    setActive(id)
    setSearch('')
    refs.current[id]?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }

  const Section = ({ id, children }) => (
    <div id={id} ref={el => refs.current[id] = el} className={styles.group}>
      {children}
    </div>
  )

  return (
    <div className={styles.layout}>

      {/* ── SIDEBAR ── */}
      <aside className={styles.sidebar}>
        {GROUPS.map(g => (
          <button
            key={g.id}
            className={`${styles.sideLink} ${active === g.id ? styles.sideLinkActive : ''}`}
            onClick={() => scrollTo(g.id)}
          >
            {g.sidebar}
          </button>
        ))}
        <div className={styles.sideSwagger}>
          <a href={SWAGGER} target="_blank" rel="noreferrer" className={styles.swaggerLink}>
            Full Swagger UI ↗
          </a>
        </div>
      </aside>

      {/* ── CONTENT ── */}
      <main className={styles.content}>

        {/* ── SEARCH BAR ── */}
        <div className={styles.searchWrap}>
          <span className={styles.searchIcon}>⌕</span>
          <input
            className={styles.searchInput}
            type="text"
            placeholder="Search endpoints — try 'appointment', 'POST', 'kafka'..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
          {search && (
            <button className={styles.searchClear} onClick={() => setSearch('')}>✕</button>
          )}
        </div>

        {/* ── SEARCH RESULTS ── */}
        {search && (
          <div className={styles.searchResults}>
            {searchResults.length === 0 ? (
              <div className={styles.searchEmpty}>No endpoints match "{search}"</div>
            ) : (
              <>
                <div className={styles.searchCount}>{searchResults.length} result{searchResults.length !== 1 ? 's' : ''}</div>
                {searchResults.map((ep, i) => (
                  <button
                    key={i}
                    className={styles.searchResultItem}
                    onClick={() => scrollTo(ep.group)}
                  >
                    <span
                      className={styles.searchMethod}
                      style={{ color: METHOD_COLOR[ep.method] }}
                    >{ep.method}</span>
                    <span className={styles.searchPath}>{ep.path}</span>
                    <span className={styles.searchSummary}>{ep.summary}</span>
                    <span className={styles.searchJump}>Jump →</span>
                  </button>
                ))}
              </>
            )}
          </div>
        )}

        {/* INTRO */}
        <Section id="intro">
          <h1 className={styles.docsTitle}>
            Hospito API <span className={styles.version}>v1.0</span>
          </h1>
          <p className={styles.docsIntroText}>
            Enterprise-grade hospital franchise management system. Supports the complete patient
            journey — from registration and appointment booking to consultation, medical records,
            and billing — all within a unified, multi-branch ecosystem.
          </p>

          <div className={styles.baseUrlBar}>
            <span className={styles.baseLabel}>BASE URL</span>
            <span className={styles.baseVal}>https://hospito-production.up.railway.app</span>
          </div>

          <div className={styles.authCard}>
            <div className={styles.authCardHeader}>
              <span className={styles.authBadge}>AUTH</span>
              <span className={styles.authTitle}>How to Authenticate</span>
            </div>
            <div className={styles.authCardBody}>
              <p className={styles.authDesc}>
                All protected endpoints require a Bearer token in the Authorization header.
                First register a user, then login to receive a JWT token valid for 24 hours.
              </p>
              <CodeBlock
                lang="bash"
                code={`# 1. Login to get your token
curl -X POST 'https://hospito-production.up.railway.app/api/v1/auth/login' \\
  -H 'Content-Type: application/json' \\
  -d '{"email":"admin@hospito.com","password":"password"}'

# 2. Use token in all subsequent requests
curl -H 'Authorization: Bearer <your-token>' \\
  https://hospito-production.up.railway.app/api/v1/branches`}
              />
            </div>
          </div>
        </Section>

        {/* AUTH */}
        <Section id="auth">
          <div className={styles.groupTitle}>Authentication</div>
          <div className={styles.groupDesc}>Register users and obtain JWT tokens. All other endpoints require the token returned from /auth/login.</div>
          <EndpointCard
            method="POST" path="/api/v1/auth/register"
            summary="Create a new user account" isPublic
            description="Register a new user. Roles: SUPER_ADMIN, BRANCH_ADMIN, DOCTOR, PATIENT. The role determines which endpoints are accessible."
            request={`{
  "email":    "doctor@hospito.com",
  "password": "securepass123",
  "role":     "DOCTOR"
}`}
            response={`{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "userId": 14,
    "email":  "doctor@hospito.com"
  }
}`}
          />
          <EndpointCard
            method="POST" path="/api/v1/auth/login"
            summary="Authenticate and receive JWT" isPublic
            description="Returns a JWT token valid for 24 hours. Include it in the Authorization header as 'Bearer <token>' for all protected requests."
            response={`{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "role":  "DOCTOR"
  }
}`}
          />
        </Section>

        {/* CHAIN */}
        <Section id="chain">
          <div className={styles.groupTitle}>Hospital Chain</div>
          <div className={styles.groupDesc}>Manage the top-level hospital chain entity. Only one chain is allowed per deployment — singleton enforcement built in.</div>
          <EndpointCard
            method="POST" path="/api/v1/chains"
            summary="Create hospital chain" auth="SUPER_ADMIN"
            description="Creates the hospital chain. Only one chain can exist — subsequent calls return 409 Conflict. This is the root entity for the entire system."
            request={`{
  "name":           "Apollo Hospitals",
  "registrationNo": "REG-2024-001",
  "foundedYear":    1983,
  "email":          "admin@apollo.com"
}`}
          />
          <EndpointCard
            method="GET" path="/api/v1/chains"
            summary="Get chain details" auth="Any Role"
            description="Returns the hospital chain details including name, registration number, founded year, and contact email."
          />
          <EndpointCard
            method="PUT" path="/api/v1/chains/{id}"
            summary="Update chain details" auth="SUPER_ADMIN"
            request={`{
  "name":  "Apollo Hospitals Group",
  "email": "new@apollo.com"
}`}
          />
        </Section>

        {/* BRANCH */}
        <Section id="branch">
          <div className={styles.groupTitle}>Branches</div>
          <div className={styles.groupDesc}>Create and manage hospital branches under the chain. Branches can have doctors and patients assigned to them.</div>
          <EndpointCard
            method="POST" path="/api/v1/branches"
            summary="Create a branch" auth="SUPER_ADMIN"
            request={`{
  "name":         "Apollo Bandra",
  "address":      "Link Road, Bandra West",
  "city":         "Mumbai",
  "contactPhone": "9876543210"
}`}
          />
          <EndpointCard
            method="GET" path="/api/v1/branches"
            summary="List all branches" auth="Any Role"
            description="Returns all active branches. Soft-deleted branches (isActive=false) are excluded by default."
          />
          <EndpointCard
            method="DELETE" path="/api/v1/branches/{id}"
            summary="Soft-delete a branch" auth="SUPER_ADMIN"
            description="Sets isActive=false. The branch is hidden from listings but data is preserved."
          />
        </Section>

        {/* DOCTOR */}
        <Section id="doctor">
          <div className={styles.groupTitle}>Doctors</div>
          <div className={styles.groupDesc}>Register doctors, manage weekly availability, and transfer across branches. License numbers are unique system-wide.</div>
          <EndpointCard
            method="POST" path="/api/v1/doctors"
            summary="Register a doctor" auth="BRANCH_ADMIN"
            request={`{
  "name":           "Dr. Priya Sharma",
  "specialization": "Cardiology",
  "email":          "priya@apollo.com",
  "licenseNo":      "MH-2019-4421",
  "primaryBranchId": 2
}`}
          />
          <EndpointCard
            method="PUT" path="/api/v1/doctors/{id}/availability"
            summary="Set weekly availability" auth="DOCTOR"
            description="Full-replace PUT for weekly schedule. Replaces all existing slots. Each slot defines a day, start/end time, and slot duration."
            request={`[
  {
    "dayOfWeek":     "MONDAY",
    "startTime":     "09:00",
    "endTime":       "17:00",
    "slotDuration":  30
  },
  {
    "dayOfWeek":     "WEDNESDAY",
    "startTime":     "10:00",
    "endTime":       "14:00",
    "slotDuration":  20
  }
]`}
          />
          <EndpointCard
            method="PATCH" path="/api/v1/doctors/{id}/transfer"
            summary="Transfer to another branch" auth="SUPER_ADMIN"
            description="Updates the doctor's primary branch and adds the new branch to their branch set. Previous assignments are preserved."
            request={`{ "targetBranchId": 5 }`}
          />
        </Section>

        {/* PATIENT */}
        <Section id="patient">
          <div className={styles.groupTitle}>Patients</div>
          <div className={styles.groupDesc}>Patient self-registration is public. Medical records and insurance are tied to individual patients and accessible across branches.</div>
          <EndpointCard
            method="POST" path="/api/v1/patients/register"
            summary="Register a patient" isPublic
            request={`{
  "name":        "Rahul Mehta",
  "email":       "rahul@email.com",
  "dateOfBirth": "1990-05-14",
  "gender":      "MALE",
  "bloodGroup":  "O_POSITIVE"
}`}
          />
          <EndpointCard
            method="POST" path="/api/v1/patients/{id}/insurance"
            summary="Add insurance policy" auth="PATIENT / BRANCH_ADMIN"
            request={`{
  "policyNumber": "HDFC-2024-88721",
  "provider":     "HDFC Ergo",
  "validTill":    "2027-12-31"
}`}
          />
          <EndpointCard
            method="GET" path="/api/v1/patients/{id}/medical-records"
            summary="Get medical records" auth="DOCTOR / PATIENT"
            description="Returns full medical history across all branches — diagnoses, prescriptions, and clinical notes recorded by doctors."
          />
          <EndpointCard
            method="POST" path="/api/v1/patients/{id}/medical-records"
            summary="Add medical record" auth="DOCTOR"
            request={`{
  "appointmentId": 23,
  "diagnosis":     "Hypertension Stage 1",
  "prescription":  "Amlodipine 5mg once daily",
  "notes":         "Follow up in 4 weeks"
}`}
          />
        </Section>

        {/* APPOINTMENT */}
        <Section id="appointment">
          <div className={styles.groupTitle}>Appointments</div>
          <div className={styles.groupDesc}>Book and manage patient appointments. Conflict detection prevents double-booking. Status transitions follow a strict state machine.</div>
          <EndpointCard
            method="POST" path="/api/v1/appointments"
            summary="Book an appointment" auth="PATIENT / BRANCH_ADMIN"
            description="Creates an appointment with automatic conflict detection. If the doctor already has an appointment at the specified time, a 409 is returned. On success, a Kafka event is fired to the appointment-events topic."
            request={`{
  "doctorId":        5,
  "patientId":       12,
  "branchId":        2,
  "appointmentTime": "2026-03-15T09:30:00",
  "reason":          "Annual checkup"
}`}
            response={`{
  "success": true,
  "data": {
    "id":              47,
    "status":          "PENDING",
    "appointmentTime": "2026-03-15T09:30:00",
    "doctor":          "Dr. Priya Sharma",
    "patient":         "Rahul Mehta"
  }
}`}
          />
          <EndpointCard
            method="PATCH" path="/api/v1/appointments/{id}/status"
            summary="Update appointment status" auth="DOCTOR / BRANCH_ADMIN"
            description="Transitions appointment through its state machine. Invalid transitions are rejected."
            extra={
              <CodeBlock lang="State Machine" code={`PENDING → CONFIRMED → IN_PROGRESS → COMPLETED
                              ↘ NO_SHOW
                 ↘ CANCELLED`} />
            }
            request={`{ "status": "CONFIRMED" }`}
          />
          <EndpointCard
            method="GET" path="/api/v1/appointments"
            summary="List appointments" auth="Any Role"
            description="Returns appointments filtered by role — patients see their own, doctors see theirs, admins see all."
          />
        </Section>

        {/* BILLING */}
        <Section id="billing">
          <div className={styles.groupTitle}>Billing</div>
          <div className={styles.groupDesc}>Generate bills for completed appointments. Process payments via Cash, Card, UPI, or Insurance. Each strategy applies different rules and fees.</div>
          <EndpointCard
            method="POST" path="/api/v1/bills"
            summary="Create a bill" auth="BRANCH_ADMIN"
            description="Creates a bill with line items. Only one bill per appointment — duplicate requests return 409."
            request={`{
  "appointmentId": 47,
  "items": [
    { "description": "Consultation", "quantity": 1, "unitPrice": 500.00 },
    { "description": "Blood Test",   "quantity": 1, "unitPrice": 200.00 }
  ],
  "discount": 50.00,
  "tax":      18.00
}`}
          />
          <EndpointCard
            method="POST" path="/api/v1/bills/{id}/pay"
            summary="Process payment" auth="PATIENT / BRANCH_ADMIN"
            description="Applies the selected payment strategy. Card adds 1.5% fee. Insurance applies 80% coverage and calculates the patient's share. UPI validates @ in the ID. On success, fires a billing-events Kafka message."
            request={`{
  "paymentType": "INSURANCE",
  // CASH | CARD | UPI | INSURANCE
  "upiId": null
}`}
            response={`{
  "success": true,
  "data": {
    "finalAmount":     134.00,
    "paymentType":     "INSURANCE",
    "paymentStatus":   "PAID",
    "insurancePaid":   536.00,
    "patientPaid":     134.00
  }
}`}
          />
          <EndpointCard
            method="PATCH" path="/api/v1/bills/{id}/waive"
            summary="Waive a bill" auth="SUPER_ADMIN"
            description="Waives the bill entirely. SUPER_ADMIN only. A reason note is required and preserved for audit."
            request={`{ "reason": "Patient under hardship scheme" }`}
          />
        </Section>

        {/* bottom CTA */}
        <div className={styles.bottomCta}>
          <div className={styles.bottomCtaTitle}>Want full interactive docs?</div>
          <div className={styles.bottomCtaDesc}>Every endpoint with live try-it-out, request validation, and response schemas.</div>
          <a href={SWAGGER} target="_blank" rel="noreferrer" className={styles.bottomCtaBtn}>
            Open Swagger UI ↗
          </a>
        </div>

      </main>
    </div>
  )
}
