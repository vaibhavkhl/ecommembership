import type { SubscriptionEvent } from '../types/subscription'

const LABELS: Record<SubscriptionEvent['eventType'], string> = {
  SUBSCRIBED: 'Subscribed',
  UPGRADED: 'Upgraded',
  DOWNGRADED: 'Downgraded',
  PLAN_CHANGED: 'Plan changed',
  CANCELLED: 'Cancelled',
}

function describe(event: SubscriptionEvent): string {
  if (event.eventType === 'SUBSCRIBED') return `${event.toPlanCode} · ${event.toTierCode}`
  if (event.eventType === 'CANCELLED') return `${event.fromPlanCode} · ${event.fromTierCode}`
  return `${event.fromPlanCode} · ${event.fromTierCode} → ${event.toPlanCode} · ${event.toTierCode}`
}

export function SubscriptionHistory({ events }: { events: SubscriptionEvent[] }) {
  if (events.length === 0) {
    return <p className="text-sm text-slate-500 dark:text-slate-400">No history yet.</p>
  }

  return (
    <ol className="space-y-2">
      {events.map((event) => (
        <li
          key={event.id}
          className="flex flex-wrap items-center justify-between gap-2 rounded-lg border border-slate-200 bg-white px-4 py-2.5 text-sm dark:border-slate-700 dark:bg-slate-800"
        >
          <div>
            <span className="font-medium text-slate-900 dark:text-slate-100">{LABELS[event.eventType]}</span>{' '}
            <span className="text-slate-600 dark:text-slate-400">{describe(event)}</span>
          </div>
          <div className="flex items-center gap-3 text-slate-500 dark:text-slate-400">
            {event.pricePaid != null && (
              <span>
                {event.currency} {event.pricePaid}
              </span>
            )}
            <span>{new Date(event.createdAt).toLocaleString()}</span>
          </div>
        </li>
      ))}
    </ol>
  )
}
