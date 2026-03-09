import styles from './Footer.module.css'

export default function Footer() {
  return (
    <footer className={styles.footer}>
      <div className={styles.logo}>
        HOSPI<span>TO</span>
      </div>
      <p className={styles.copy}>
        Built by Yagnik · Spring Boot 3.3 + Java 17 · Deployed on Railway
      </p>
      <div className={styles.links}>
        <a href="https://github.com/yagnik2411/hospito" target="_blank" rel="noreferrer">GitHub</a>
        <a href="https://hospito-production.up.railway.app/swagger-ui/index.html" target="_blank" rel="noreferrer">Swagger</a>
        <a href="https://hospito-production.up.railway.app/actuator/health" target="_blank" rel="noreferrer">Health</a>
      </div>
    </footer>
  )
}
