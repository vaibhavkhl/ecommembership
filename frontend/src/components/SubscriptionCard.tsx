import type { Subscription } from '../types/subscription'
import { StatusBadge } from './StatusBadge'

export function SubscriptionCard({ subscription, onCancel }: { subscription: Subscription; onCancel: () => void }) {
  return (
    <div className="max-w-md rounded-xl border border-slate-200 bg-white p-5 shadow-sm dark:border-slate-700 dark:bg-slate-800">
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-semibold text-slate-900 dark:text-slate-100">
          {subscription.plan.code} · {subscription.tier.code}
        </h3>
        <StatusBadge status={subscription.status} />
      </div>
      <dl className="mt-3 space-y-1 text-sm text-slate-600 dark:text-slate-400">
        <div className="flex justify-between">
          <dt>Start</dt>
          <dd>{new Date(subscription.startDate).toLocaleString()}</dd>
        </div>
        <div className="flex justify-between">
          <dt>End</dt>
          <dd>{new Date(subscription.endDate).toLocaleString()}</dd>
        </div>
      </dl>
      {subscription.expired && <p className="mt-3 text-sm font-medium text-red-600 dark:text-red-400">This membership has expired.</p>}
      {subscription.status === 'ACTIVE' && (
        <button
          onClick={onCancel}
          className="mt-4 rounded-md border border-red-200 px-3 py-1.5 text-sm font-medium text-red-600 transition hover:bg-red-50 dark:border-red-900 dark:text-red-400 dark:hover:bg-red-900/20"
        >
          Cancel membership
        </button>
      )}
    </div>
  )
}
