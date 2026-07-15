import { useState, type FormEvent } from 'react'
import { adminApi } from '../../api/admin'
import { ApiError } from '../../api/client'
import type { BenefitDefinition } from '../../types/admin'

const inputClass =
  'rounded-md border border-slate-300 bg-white px-3 py-1.5 text-sm text-slate-900 placeholder:text-slate-400 focus:border-indigo-500 focus:outline-none dark:border-slate-600 dark:bg-slate-800 dark:text-slate-100'

export function BenefitDefinitionsPanel({
  definitions,
  onCreated,
}: {
  definitions: BenefitDefinition[]
  onCreated: () => void
}) {
  const [code, setCode] = useState('')
  const [description, setDescription] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      await adminApi.createBenefitDefinition(code.trim().toUpperCase().replace(/\s+/g, '_'), description.trim())
      setCode('')
      setDescription('')
      onCreated()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : String(err))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="rounded-lg border border-slate-200 bg-white p-5 dark:border-slate-700 dark:bg-slate-800">
      <h3 className="mb-3 text-lg font-semibold text-slate-900 dark:text-slate-100">Benefit definitions</h3>
      <p className="mb-4 text-sm text-slate-500 dark:text-slate-400">
        The catalog of benefit types (e.g. <code className="rounded bg-slate-100 px-1 dark:bg-slate-700">DISCOUNT_PERCENT</code>). Assign
        them to tiers below.
      </p>

      <form onSubmit={handleSubmit} className="mb-4 flex flex-wrap items-end gap-2">
        <div>
          <label className="block text-xs font-medium text-slate-600 dark:text-slate-400">Code</label>
          <input
            required
            value={code}
            onChange={(e) => setCode(e.target.value)}
            placeholder="BIRTHDAY_BONUS"
            className={inputClass}
          />
        </div>
        <div>
          <label className="block text-xs font-medium text-slate-600 dark:text-slate-400">Description</label>
          <input
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Bonus perk on your birthday month"
            className={`${inputClass} w-64`}
          />
        </div>
        <button
          type="submit"
          disabled={submitting}
          className="rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold text-white transition hover:bg-indigo-500 disabled:opacity-50"
        >
          Add benefit
        </button>
      </form>

      {error && <p className="mb-3 text-sm text-red-600 dark:text-red-400">{error}</p>}

      <table className="w-full text-left text-sm">
        <thead>
          <tr className="text-xs font-semibold uppercase text-slate-500 dark:text-slate-400">
            <th className="py-1.5 pr-4">Code</th>
            <th className="py-1.5">Description</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-100 dark:divide-slate-700">
          {definitions.map((def) => (
            <tr key={def.id}>
              <td className="py-1.5 pr-4 font-medium text-slate-900 dark:text-slate-100">{def.code}</td>
              <td className="py-1.5 text-slate-600 dark:text-slate-400">{def.description}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
