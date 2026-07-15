import type { Subscription } from '../types/subscription'
import { StatusBadge } from './StatusBadge'

export function SubscriptionCard({ subscription, onCancel }: { subscription: Subscription; onCancel: () => void }) {
  return (
    <div style={{ border: '1px solid #d0d7de', borderRadius: 8, padding: 16, maxWidth: 480 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h3 style={{ margin: 0 }}>
          {subscription.plan.code} · {subscription.tier.code}
        </h3>
        <StatusBadge status={subscription.status} />
      </div>
      <p>Start: {new Date(subscription.startDate).toLocaleString()}</p>
      <p>End: {new Date(subscription.endDate).toLocaleString()}</p>
      {subscription.expired && <p style={{ color: '#cf222e' }}>This membership has expired.</p>}
      {subscription.status === 'ACTIVE' && (
        <button onClick={onCancel} style={{ color: '#cf222e' }}>
          Cancel membership
        </button>
      )}
    </div>
  )
}
