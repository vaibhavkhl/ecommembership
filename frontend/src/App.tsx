import { useState } from 'react'
import { useCurrentUser } from './hooks/useCurrentUser'
import { PlansPage } from './pages/PlansPage'
import { MyMembershipPage } from './pages/MyMembershipPage'

type Tab = 'plans' | 'membership'

function App() {
  const { userId, setUserId } = useCurrentUser()
  const [tab, setTab] = useState<Tab>('membership')
  const [refreshKey, setRefreshKey] = useState(0)

  return (
    <div style={{ maxWidth: 900, margin: '0 auto', padding: 24, fontFamily: 'sans-serif' }}>
      <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <h1 style={{ margin: 0 }}>Membership</h1>
        <label>
          User ID:{' '}
          <input
            type="number"
            value={userId}
            onChange={(e) => setUserId(Number(e.target.value))}
            style={{ width: 60 }}
          />
        </label>
      </header>

      <nav style={{ display: 'flex', gap: 8, marginBottom: 24 }}>
        <button onClick={() => setTab('membership')} disabled={tab === 'membership'}>
          My Membership
        </button>
        <button onClick={() => setTab('plans')} disabled={tab === 'plans'}>
          Plans & Tiers
        </button>
      </nav>

      {tab === 'plans' && (
        <PlansPage
          userId={userId}
          onSubscribed={() => {
            setRefreshKey((k) => k + 1)
            setTab('membership')
          }}
        />
      )}
      {tab === 'membership' && <MyMembershipPage userId={userId} refreshKey={refreshKey} />}
    </div>
  )
}

export default App
