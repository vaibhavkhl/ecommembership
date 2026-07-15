import { useState } from 'react'
import { useCurrentUser } from './hooks/useCurrentUser'
import { PlansPage } from './pages/PlansPage'
import { MyMembershipPage } from './pages/MyMembershipPage'
import { AdminPage } from './pages/AdminPage'

type Tab = 'plans' | 'membership' | 'admin'

const TABS: { key: Tab; label: string }[] = [
  { key: 'membership', label: 'My Membership' },
  { key: 'plans', label: 'Plans & Tiers' },
  { key: 'admin', label: 'Admin' },
]

function App() {
  const { userId, setUserId } = useCurrentUser()
  const [tab, setTab] = useState<Tab>('membership')
  const [refreshKey, setRefreshKey] = useState(0)

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-900">
      <div className="mx-auto max-w-4xl px-6 py-8">
        <header className="mb-6 flex items-center justify-between">
          <h1 className="text-2xl font-bold text-slate-900 dark:text-slate-100">Membership</h1>
          <label className="flex items-center gap-2 text-sm text-slate-600 dark:text-slate-400">
            User ID
            <input
              type="number"
              value={userId}
              onChange={(e) => setUserId(Number(e.target.value))}
              className="w-16 rounded-md border border-slate-300 bg-white px-2 py-1 text-slate-900 focus:border-indigo-500 focus:outline-none dark:border-slate-600 dark:bg-slate-800 dark:text-slate-100"
            />
          </label>
        </header>

        <nav className="mb-6 flex gap-1 border-b border-slate-200 dark:border-slate-700">
          {TABS.map(({ key, label }) => (
            <button
              key={key}
              onClick={() => setTab(key)}
              className={`-mb-px border-b-2 px-3 py-2 text-sm font-medium transition ${
                tab === key
                  ? 'border-indigo-600 text-indigo-600 dark:border-indigo-400 dark:text-indigo-400'
                  : 'border-transparent text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-slate-200'
              }`}
            >
              {label}
            </button>
          ))}
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
        {tab === 'admin' && <AdminPage />}
      </div>
    </div>
  )
}

export default App
