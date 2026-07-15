import type { SubscriptionStatus } from '../types/subscription'

const COLORS: Record<SubscriptionStatus, string> = {
  ACTIVE: '#1a7f37',
  CANCELLED: '#9a6700',
  EXPIRED: '#cf222e',
  PENDING: '#57606a',
}

export function StatusBadge({ status }: { status: SubscriptionStatus }) {
  return (
    <span
      style={{
        color: '#fff',
        background: COLORS[status],
        borderRadius: 999,
        padding: '2px 10px',
        fontSize: 12,
        fontWeight: 600,
      }}
    >
      {status}
    </span>
  )
}
