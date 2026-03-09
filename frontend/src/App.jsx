import { useState } from 'react'
import Navbar from './components/Navbar'
import Home from './pages/Home'
import Docs from './pages/Docs'

export default function App() {
  const [page, setPage] = useState('home')

  return (
    <>
      <Navbar page={page} setPage={setPage} />
      {page === 'home' ? <Home setPage={setPage} /> : <Docs />}
    </>
  )
}
