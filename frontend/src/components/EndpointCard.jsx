import { useState } from 'react'
import styles from './EndpointCard.module.css'

const METHOD_CLASS = {
  GET: styles.get,
  POST: styles.post,
  PUT: styles.put,
  PATCH: styles.patch,
  DELETE: styles.del,
}

export default function EndpointCard({ method, path, summary, auth, isPublic, description, request, response, extra }) {
  const [open, setOpen] = useState(false)

  return (
    <div className={`${styles.card} ${open ? styles.open : ''}`} onClick={() => setOpen(o => !o)}>
      <div className={styles.header}>
        <span className={`${styles.badge} ${METHOD_CLASS[method] || ''}`}>{method}</span>
        <span className={styles.path}>{path}</span>
        <span className={styles.summary}>{summary}</span>
        <span className={`${styles.auth} ${isPublic ? styles.public : ''}`}>
          {isPublic ? '◎ Public' : `🔒 ${auth}`}
        </span>
      </div>

      {open && (
        <div className={styles.body} onClick={e => e.stopPropagation()}>
          {description && <p className={styles.desc}>{description}</p>}
          {request && (
            <CodeBlock lang="Request Body" code={request} />
          )}
          {response && (
            <CodeBlock lang="Response · 200/201" code={response} />
          )}
          {extra}
        </div>
      )}
    </div>
  )
}

export function CodeBlock({ lang, code }) {
  const [copied, setCopied] = useState(false)

  const handleCopy = (e) => {
    e.stopPropagation()
    navigator.clipboard.writeText(code).then(() => {
      setCopied(true)
      setTimeout(() => setCopied(false), 2000)
    })
  }

  return (
    <div className={styles.codeBlock}>
      <div className={styles.codeHeader}>
        <span className={styles.codeLang}>{lang}</span>
        <button className={styles.codeCopy} onClick={handleCopy}>
          {copied ? 'copied!' : 'copy'}
        </button>
      </div>
      <pre className={styles.codePre}><code>{code}</code></pre>
    </div>
  )
}
