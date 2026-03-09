import Footer from '../components/Footer'
import styles from './Home.module.css'

const SWAGGER = 'https://hospito-production.up.railway.app/swagger-ui/index.html'
const GITHUB  = 'https://github.com/yagnik2411/hospito'

const FEATURES = [
  { icon: '🔐', name: 'Auth + RBAC', desc: 'JWT stateless authentication with Spring Security 6. Four roles with method-level @PreAuthorize enforcement across every endpoint.', tag: 'Spring Security' },
  { icon: '🏥', name: 'Chain + Branch', desc: 'Singleton chain enforcement. Unlimited branches with soft-delete, city grouping, and cross-branch doctor assignments.', tag: 'Multi-tenant' },
  { icon: '👨‍⚕️', name: 'Doctor Management', desc: 'License uniqueness, weekly availability scheduling, cross-branch transfers, specialization tracking and multi-branch assignment.', tag: 'Scheduling' },
  { icon: '📅', name: 'Appointments', desc: 'JPQL conflict detection prevents double-booking. Full status state machine: PENDING → CONFIRMED → IN_PROGRESS → COMPLETED.', tag: 'Conflict Detection' },
  { icon: '💳', name: 'Billing + Payments', desc: 'Strategy pattern for Cash, Card, UPI, and Insurance payments. Line-item bills, tax calculation, and insurance coverage automation.', tag: 'Strategy Pattern' },
  { icon: '⚡', name: 'Kafka Notifications', desc: 'Event-driven notification microservice. Appointment and billing events fire asynchronously via Apache Kafka without coupling core logic.', tag: 'Event-Driven' },
]

const TECH = [
  { label: 'Java 17',           color: '#F89820' },
  { label: 'Spring Boot 3.3',   color: '#6DB33F' },
  { label: 'PostgreSQL 15',     color: '#336791' },
  { label: 'Redis 7',           color: '#DC382D' },
  { label: 'Apache Kafka',      color: '#808080' },
  { label: 'Docker',            color: '#2496ED' },
  { label: 'GitHub Actions',    color: '#2088FF' },
  { label: 'Railway',           color: '#7B61FF' },
  { label: 'SpringDoc OpenAPI', color: '#85EA2D' },
  { label: 'JUnit 5 + Mockito', color: '#25A162' },
]

const ARCH = [
  { label: 'Client',  items: ['HTTPS Request', '→', 'Railway Public URL'] },
  { label: 'API',     items: ['hospito-app :8080', '→', 'JWT Filter', '→', 'Controllers', '→', 'Services'] },
  { label: 'Data',    items: ['PostgreSQL :5432', '·', 'Redis :6379', '·', 'Kafka :29092'] },
  { label: 'Events',  items: ['appointment-events', '·', 'billing-events', '→', 'notification-service'] },
]

export default function Home({ setPage }) {
  return (
    <main className={styles.main}>

      {/* ── HERO ── */}
      <section className={styles.hero}>
        <div className={styles.heroBadge + ' animate-up-0'}>
          <span className={styles.statusRing} />
          Production Live · Railway · v1.0.0
        </div>

        <h1 className={styles.heroTitle + ' animate-up-1'}>
          Enterprise<br />
          <span className={styles.accentLine}>Hospital API</span>
          <span className={styles.dimLine}> Infrastructure</span>
        </h1>

        <p className={styles.heroSub + ' animate-up-2'}>
          A production-grade franchise hospital chain management system.
          Multi-branch operations, full patient lifecycle, event-driven
          notifications — deployed and live.
        </p>

        <div className={styles.heroActions + ' animate-up-3'}>
          <button className={styles.btnPrimary} onClick={() => setPage('docs')}>
            Explore API Docs <span className={styles.arrow}>→</span>
          </button>
          <a href={SWAGGER} target="_blank" rel="noreferrer" className={styles.btnOutline}>
            Swagger UI ↗
          </a>
          <a href={GITHUB} target="_blank" rel="noreferrer" className={styles.btnOutline}>
            GitHub ↗
          </a>
        </div>

        {/* floating code card */}
        <div className={styles.heroVisual + ' animate-left'}>
          <div className={styles.codeCard}>
            <div className={styles.codeCardHeader}>
              <span className={styles.dotR} /><span className={styles.dotY} /><span className={styles.dotG} />
              <span className={styles.codeFilename}>POST /api/v1/appointments</span>
            </div>
            <div className={styles.codeBody}>
              <pre>{`// Book an appointment
fetch('https://hospito-production
  .up.railway.app/api/v1
  /appointments', {
  method: 'POST',
  headers: {
    Authorization: \`Bearer \${token}\`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    doctorId: 12,
    patientId: 7,
    branchId: 3,
    appointmentTime: "2026-03-10T10:00",
    reason: "General checkup"
  })
})

// → 201 Created
// → Kafka event fired ⚡`}</pre>
            </div>
          </div>
        </div>
      </section>

      {/* ── STATS ── */}
      <div className={styles.statsBar}>
        {[
          { num: '30+', label: 'REST Endpoints' },
          { num: '4',   label: 'User Roles' },
          { num: '6',   label: 'Live Services' },
          { num: '26',  label: 'Tests Passing' },
        ].map(s => (
          <div key={s.label} className={styles.statItem}>
            <div className={styles.statNum}>{s.num}</div>
            <div className={styles.statLabel}>{s.label}</div>
          </div>
        ))}
      </div>

      {/* ── FEATURES ── */}
      <section className={styles.section}>
        <div className={styles.sectionLabel}>// Core Modules</div>
        <h2 className={styles.sectionTitle}>Everything a hospital<br />chain needs</h2>
        <p className={styles.sectionSub}>
          Built module-first. Every domain is self-contained with its own
          controllers, services, repositories, and DTOs.
        </p>
        <div className={styles.featuresGrid}>
          {FEATURES.map(f => (
            <div key={f.name} className={styles.featureCard}>
              <div className={styles.featureIcon}>{f.icon}</div>
              <div className={styles.featureName}>{f.name}</div>
              <div className={styles.featureDesc}>{f.desc}</div>
              <span className={styles.featureTag}>{f.tag}</span>
            </div>
          ))}
        </div>
      </section>

      {/* ── TECH STACK ── */}
      <section className={styles.techSection}>
        <div className={styles.sectionLabel}>// Tech Stack</div>
        <h2 className={styles.sectionTitleSm}>Production-grade<br />from day one</h2>
        <div className={styles.techGrid}>
          {TECH.map(t => (
            <div key={t.label} className={styles.techPill}>
              <span className={styles.techDot} style={{ background: t.color }} />
              {t.label}
            </div>
          ))}
        </div>
      </section>

      {/* ── ARCHITECTURE ── */}
      <section className={styles.archSection}>
        <div className={styles.sectionLabel}>// Architecture</div>
        <h2 className={styles.sectionTitleSm}>How it all fits<br />together</h2>
        <div className={styles.archDiagram}>
          {ARCH.map(row => (
            <div key={row.label} className={styles.archRow}>
              <span className={styles.archLabel}>{row.label}</span>
              {row.items.map((item, i) => (
                item === '→' || item === '·'
                  ? <span key={i} className={styles.archArrow}>{item}</span>
                  : <div key={i} className={`${styles.archBox} ${
                      item === 'hospito-app :8080' || item === 'notification-service' || item === 'Railway Public URL'
                        ? styles.archPrimary : ''
                    }`}>{item}</div>
              ))}
            </div>
          ))}
        </div>
      </section>

      {/* ── CTA ── */}
      <section className={styles.ctaSection}>
        <div className={styles.ctaGlow} />
        <h2 className={styles.sectionTitle}>Ready to explore?</h2>
        <p className={styles.sectionSub} style={{ margin: '0 auto 48px' }}>
          Full OpenAPI documentation with live try-it-out. Authenticate with
          JWT and hit every endpoint directly from the browser.
        </p>
        <div className={styles.heroActions} style={{ justifyContent: 'center' }}>
          <button className={styles.btnPrimary} onClick={() => setPage('docs')}>
            Browse API Docs <span className={styles.arrow}>→</span>
          </button>
          <a href={SWAGGER} target="_blank" rel="noreferrer" className={styles.btnOutline}>
            Open Swagger UI ↗
          </a>
        </div>
      </section>

      <Footer />
    </main>
  )
}
