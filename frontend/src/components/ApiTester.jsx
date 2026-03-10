import { useState } from 'react'
import styles from './ApiTester.module.css'

const BASE = 'https://hospito.up.railway.app'

const ENDPOINTS = [
  {
    id: 'login',
    method: 'POST',
    label: 'Login',
    path: '/api/v1/auth/login',
    isPublic: true,
    fields: [
      { name: 'email',    type: 'text',     placeholder: 'admin@hospito.com', default: 'test@hospito.com' },
      { name: 'password', type: 'password', placeholder: '••••••••',          default: 'password123' },
    ],
    buildBody: (f) => ({ email: f.email, password: f.password }),
  },
  {
    id: 'register',
    method: 'POST',
    label: 'Register User',
    path: '/api/v1/auth/register',
    isPublic: true,
    fields: [
      { name: 'email',    type: 'text',   placeholder: 'user@example.com', default: '' },
      { name: 'password', type: 'text',   placeholder: 'password123',      default: '' },
      { name: 'role',     type: 'select', options: ['PATIENT', 'DOCTOR', 'BRANCH_ADMIN', 'SUPER_ADMIN'], default: 'PATIENT' },
    ],
    buildBody: (f) => ({ email: f.email, password: f.password, role: f.role }),
  },
  {
    id: 'branches',
    method: 'GET',
    label: 'List Branches',
    path: '/api/v1/branches',
    requiresToken: true,
    fields: [],
    buildBody: () => null,
  },
  {
    id: 'doctors',
    method: 'GET',
    label: 'List Doctors',
    path: '/api/v1/doctors',
    requiresToken: true,
    fields: [],
    buildBody: () => null,
  },
  {
    id: 'patients',
    method: 'GET',
    label: 'List Patients',
    path: '/api/v1/patients',
    requiresToken: true,
    fields: [],
    buildBody: () => null,
  },
  {
    id: 'appointments',
    method: 'GET',
    label: 'List Appointments',
    path: '/api/v1/appointments',
    requiresToken: true,
    fields: [],
    buildBody: () => null,
  },
  {
    id: 'chain',
    method: 'GET',
    label: 'Get Chain',
    path: '/api/v1/chains',
    requiresToken: true,
    fields: [],
    buildBody: () => null,
  },
]

const METHOD_COLOR = { GET: '#4D9EFF', POST: '#00FFB2', PUT: '#FFD166', PATCH: '#A78BFA', DELETE: '#FF4D6A' }

function syntaxHighlight(json) {
  return json
    .replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+-]?\d+)?)/g, (match) => {
      let cls = 'num'
      if (/^"/.test(match)) {
        cls = /:$/.test(match) ? 'key' : 'str'
      } else if (/true|false/.test(match)) {
        cls = 'bool'
      } else if (/null/.test(match)) {
        cls = 'null'
      }
      return `<span class="json-${cls}">${match}</span>`
    })
}

export default function ApiTester() {
  const [selected, setSelected]   = useState(ENDPOINTS[0])
  const [fields, setFields]       = useState(() => Object.fromEntries(ENDPOINTS[0].fields.map(f => [f.name, f.default])))
  const [token, setToken]         = useState('')
  const [loading, setLoading]     = useState(false)
  const [response, setResponse]   = useState(null)
  const [status, setStatus]       = useState(null)
  const [elapsed, setElapsed]     = useState(null)
  const [error, setError]         = useState(null)

  const selectEndpoint = (ep) => {
    setSelected(ep)
    setFields(Object.fromEntries(ep.fields.map(f => [f.name, f.default])))
    setResponse(null)
    setStatus(null)
    setElapsed(null)
    setError(null)
  }

  const handleField = (name, val) => setFields(f => ({ ...f, [name]: val }))

  const run = async () => {
    setLoading(true)
    setResponse(null)
    setError(null)
    setStatus(null)
    setElapsed(null)

    const t0 = performance.now()
    try {
      const body = selected.buildBody(fields)
      const headers = { 'Content-Type': 'application/json' }
      if (selected.requiresToken && token) headers['Authorization'] = `Bearer ${token}`

      const res = await fetch(`${BASE}${selected.path}`, {
        method: selected.method,
        headers,
        body: body ? JSON.stringify(body) : undefined,
      })

      const ms = Math.round(performance.now() - t0)
      setElapsed(ms)
      setStatus(res.status)

      let data
      const ct = res.headers.get('content-type') || ''
      if (ct.includes('application/json')) {
        data = await res.json()
      } else {
        data = { raw: await res.text() }
      }

      // auto-capture token from login
      if (selected.id === 'login' && data?.data?.token) {
        setToken(data.data.token)
      }

      setResponse(data)
    } catch (e) {
      setElapsed(Math.round(performance.now() - t0))
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  const isOk = status && status >= 200 && status < 300

  return (
    <div className={styles.tester}>
      <div className={styles.header}>
        <div className={styles.headerLeft}>
          <span className={styles.headerIcon}>⚡</span>
          <div>
            <div className={styles.headerTitle}>Live API Tester</div>
            <div className={styles.headerSub}>Hitting real Railway production endpoints</div>
          </div>
        </div>
        <div className={styles.liveTag}>
          <span className={styles.liveDot} />
          LIVE
        </div>
      </div>

      <div className={styles.body}>
        {/* endpoint selector */}
        <div className={styles.epList}>
          {ENDPOINTS.map(ep => (
            <button
              key={ep.id}
              className={`${styles.epBtn} ${selected.id === ep.id ? styles.epBtnActive : ''}`}
              onClick={() => selectEndpoint(ep)}
            >
              <span className={styles.epMethod} style={{ color: METHOD_COLOR[ep.method] }}>{ep.method}</span>
              <span className={styles.epLabel}>{ep.label}</span>
            </button>
          ))}
        </div>

        {/* right panel */}
        <div className={styles.panel}>
          {/* URL bar */}
          <div className={styles.urlBar}>
            <span className={styles.urlMethod} style={{ color: METHOD_COLOR[selected.method] }}>{selected.method}</span>
            <span className={styles.urlPath}>{BASE}{selected.path}</span>
          </div>

          {/* token field if needed */}
          {selected.requiresToken && (
            <div className={styles.tokenRow}>
              <span className={styles.tokenLabel}>Bearer Token</span>
              <input
                className={styles.tokenInput}
                type="text"
                placeholder="Paste JWT token here (or run Login first to auto-fill)"
                value={token}
                onChange={e => setToken(e.target.value)}
              />
            </div>
          )}

          {/* fields */}
          {selected.fields.length > 0 && (
            <div className={styles.fields}>
              {selected.fields.map(f => (
                <div key={f.name} className={styles.fieldRow}>
                  <label className={styles.fieldLabel}>{f.name}</label>
                  {f.type === 'select' ? (
                    <select
                      className={styles.fieldInput}
                      value={fields[f.name]}
                      onChange={e => handleField(f.name, e.target.value)}
                    >
                      {f.options.map(o => <option key={o}>{o}</option>)}
                    </select>
                  ) : (
                    <input
                      className={styles.fieldInput}
                      type={f.type}
                      placeholder={f.placeholder}
                      value={fields[f.name]}
                      onChange={e => handleField(f.name, e.target.value)}
                    />
                  )}
                </div>
              ))}
            </div>
          )}

          {/* send button */}
          <button className={styles.sendBtn} onClick={run} disabled={loading}>
            {loading
              ? <><span className={styles.spinner} /> Sending...</>
              : <>Send Request →</>
            }
          </button>

          {/* response */}
          {(response || error) && (
            <div className={styles.response}>
              <div className={styles.responseMeta}>
                <span className={`${styles.statusBadge} ${isOk ? styles.statusOk : styles.statusErr}`}>
                  {status}
                </span>
                {elapsed != null && (
                  <span className={styles.elapsed}>{elapsed}ms</span>
                )}
                {selected.id === 'login' && response?.data?.token && (
                  <span className={styles.autoCapture}>✓ Token auto-captured</span>
                )}
              </div>
              <div className={styles.responseBody}>
                {error
                  ? <span style={{ color: 'var(--red)' }}>{error}</span>
                  : <pre
                      className={styles.responsePre}
                      dangerouslySetInnerHTML={{
                        __html: syntaxHighlight(JSON.stringify(response, null, 2))
                      }}
                    />
                }
              </div>
            </div>
          )}

          {!response && !error && !loading && (
            <div className={styles.placeholder}>
              <div className={styles.placeholderIcon}>↑</div>
              <div>Fill in the fields and send your request</div>
              {selected.id === 'login' && (
                <div className={styles.placeholderTip}>💡 Login first — token auto-fills for all protected endpoints</div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
