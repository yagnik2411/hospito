import styles from './Navbar.module.css'

const SWAGGER_URL = 'https://hospito.up.railway.app/swagger-ui/index.html'

export default function Navbar({ page, setPage }) {
  return (
    <nav className={styles.nav}>
      <div className={styles.logo}>
        <span className={styles.dot} />
        HOSPI<span className={styles.accent}>TO</span>
      </div>

      <div className={styles.links}>
        <button
          className={`${styles.ghost} ${page === 'home' ? styles.active : ''}`}
          onClick={() => setPage('home')}
        >
          Home
        </button>
        <button
          className={`${styles.ghost} ${page === 'docs' ? styles.active : ''}`}
          onClick={() => setPage('docs')}
        >
          API Docs
        </button>
        <a href={SWAGGER_URL} target="_blank" rel="noreferrer" className={styles.primary}>
          Try Live API ↗
        </a>
      </div>
    </nav>
  )
}
