import { useEffect, useRef, useState } from 'react'
import styles from './ArchDiagram.module.css'

// Node definitions [id, label, x, y, primary]
const NODES = [
  { id: 'client',   label: 'Client',               x: 60,  y: 40,  primary: false },
  { id: 'railway',  label: 'Railway\nPublic URL',   x: 240, y: 40,  primary: true  },
  { id: 'hospito',  label: 'hospito-app\n:8080',    x: 440, y: 40,  primary: true  },
  { id: 'jwt',      label: 'JWT Filter',            x: 640, y: 40,  primary: false },
  { id: 'pg',       label: 'PostgreSQL\n:5432',     x: 120, y: 180, primary: false },
  { id: 'redis',    label: 'Redis\n:6379',          x: 320, y: 180, primary: false },
  { id: 'kafka',    label: 'Kafka\n:29092',         x: 520, y: 180, primary: false },
  { id: 'notif',    label: 'notification\nservice', x: 720, y: 180, primary: true  },
]

// Edges [from, to, label, animated]
const EDGES = [
  { from: 'client',  to: 'railway', label: 'HTTPS',    animated: false },
  { from: 'railway', to: 'hospito', label: 'proxy',    animated: false },
  { from: 'hospito', to: 'jwt',     label: 'filter',   animated: false },
  { from: 'hospito', to: 'pg',      label: '',         animated: false },
  { from: 'hospito', to: 'redis',   label: '',         animated: false },
  { from: 'hospito', to: 'kafka',   label: '',         animated: true  },
  { from: 'kafka',   to: 'notif',   label: 'consume',  animated: true  },
]

const NODE_W = 110
const NODE_H = 52

function getCenter(node) {
  return { cx: node.x + NODE_W / 2, cy: node.y + NODE_H / 2 }
}

export default function ArchDiagram() {
  const [hoveredNode, setHoveredNode] = useState(null)
  const [tick, setTick] = useState(0)
  const rafRef = useRef(null)
  const startRef = useRef(Date.now())

  useEffect(() => {
    const animate = () => {
      setTick(Date.now() - startRef.current)
      rafRef.current = requestAnimationFrame(animate)
    }
    rafRef.current = requestAnimationFrame(animate)
    return () => cancelAnimationFrame(rafRef.current)
  }, [])

  return (
    <div className={styles.wrapper}>
      <svg
        className={styles.svg}
        viewBox="0 0 860 260"
        xmlns="http://www.w3.org/2000/svg"
      >
        <defs>
          <marker id="arrow" markerWidth="8" markerHeight="8" refX="6" refY="3" orient="auto">
            <path d="M0,0 L0,6 L8,3 z" fill="rgba(255,255,255,0.18)" />
          </marker>
          <marker id="arrow-accent" markerWidth="8" markerHeight="8" refX="6" refY="3" orient="auto">
            <path d="M0,0 L0,6 L8,3 z" fill="#00FFB2" />
          </marker>
          <filter id="glow">
            <feGaussianBlur stdDeviation="3" result="blur" />
            <feMerge><feMergeNode in="blur"/><feMergeNode in="SourceGraphic"/></feMerge>
          </filter>
        </defs>

        {/* edges */}
        {EDGES.map((edge, i) => {
          const from = NODES.find(n => n.id === edge.from)
          const to   = NODES.find(n => n.id === edge.to)
          if (!from || !to) return null
          const { cx: x1, cy: y1 } = getCenter(from)
          const { cx: x2, cy: y2 } = getCenter(to)

          const dx = x2 - x1, dy = y2 - y1
          const len = Math.sqrt(dx * dx + dy * dy)
          const ux = dx / len, uy = dy / len
          const pad = NODE_W / 2 + 4
          const sx = x1 + ux * pad, sy = y1 + uy * pad
          const ex = x2 - ux * pad, ey = y2 - uy * pad

          const mx = (sx + ex) / 2, my = (sy + ey) / 2

          // animated dot along path
          let dotX = null, dotY = null
          if (edge.animated) {
            const speed = 0.0008
            const t = ((tick * speed) + i * 0.3) % 1
            dotX = sx + (ex - sx) * t
            dotY = sy + (ey - sy) * t
          }

          return (
            <g key={`${edge.from}-${edge.to}`}>
              <line
                x1={sx} y1={sy} x2={ex} y2={ey}
                stroke={edge.animated ? 'rgba(0,255,178,0.35)' : 'rgba(255,255,255,0.1)'}
                strokeWidth={edge.animated ? 1.5 : 1}
                markerEnd={edge.animated ? 'url(#arrow-accent)' : 'url(#arrow)'}
                strokeDasharray={edge.animated ? '4 3' : 'none'}
              />
              <text
                x={mx} y={my - 14}
                textAnchor="middle"
                fontSize="9"
                fill="rgba(255,255,255,0.4)"
                fontFamily="DM Mono, monospace"
                letterSpacing="0.05em"
              >
                {edge.label}
              </text>
              {dotX != null && (
                <circle
                  cx={dotX} cy={dotY} r="3.5"
                  fill="#00FFB2"
                  filter="url(#glow)"
                  opacity="0.9"
                />
              )}
            </g>
          )
        })}

        {/* nodes */}
        {NODES.map(node => {
          const isHovered = hoveredNode === node.id
          const lines = node.label.split('\n')
          return (
            <g
              key={node.id}
              onMouseEnter={() => setHoveredNode(node.id)}
              onMouseLeave={() => setHoveredNode(null)}
              style={{ cursor: 'default' }}
            >
              <rect
                x={node.x} y={node.y}
                width={NODE_W} height={NODE_H}
                rx="8" ry="8"
                fill={node.primary ? 'rgba(0,255,178,0.07)' : 'rgba(13,17,23,0.9)'}
                stroke={
                  isHovered
                    ? 'rgba(0,255,178,0.7)'
                    : node.primary
                      ? 'rgba(0,255,178,0.28)'
                      : 'rgba(255,255,255,0.1)'
                }
                strokeWidth={isHovered ? 1.5 : 1}
                style={{ transition: 'stroke 0.2s, fill 0.2s' }}
              />
              {lines.map((line, li) => (
                <text
                  key={li}
                  x={node.x + NODE_W / 2}
                  y={node.y + NODE_H / 2 + (li - (lines.length - 1) / 2) * 14}
                  textAnchor="middle"
                  dominantBaseline="middle"
                  fontSize="11"
                  fill={node.primary ? '#00FFB2' : isHovered ? '#E8EDF5' : '#8B95A8'}
                  fontFamily="DM Mono, monospace"
                  style={{ transition: 'fill 0.2s' }}
                >
                  {line}
                </text>
              ))}
            </g>
          )
        })}

        {/* layer labels */}
        {[
          { label: 'INGRESS',  y: 66  },
          { label: 'DATA + EVENTS', y: 206 },
        ].map(l => (
          <text
            key={l.label}
            x="14" y={l.y}
            fontSize="8"
            fill="rgba(255,255,255,0.2)"
            fontFamily="DM Mono, monospace"
            letterSpacing="0.12em"
            transform={`rotate(-90, 14, ${l.y})`}
            textAnchor="middle"
          >
            {l.label}
          </text>
        ))}
      </svg>

      <div className={styles.legend}>
        <span className={styles.legendItem}>
          <span className={styles.legendLine} style={{ background: 'rgba(0,255,178,0.35)' }} />
          Event flow (Kafka)
        </span>
        <span className={styles.legendItem}>
          <span className={styles.legendLine} style={{ background: 'rgba(255,255,255,0.12)' }} />
          Sync call
        </span>
        <span className={styles.legendDot} />
        Animated packet
      </div>
    </div>
  )
}
