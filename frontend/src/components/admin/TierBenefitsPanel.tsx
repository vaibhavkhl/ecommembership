import { useState, type FormEvent } from 'react'
import { adminApi } from '../../api/admin'
import { ApiError } from '../../api/client'
import type { BenefitDefinition, TierBenefit } from '../../types/admin'
import type { Tier } from '../../types/catalog'

const inputClass =
  'rounded-md border border-slate-300 bg-white px-3 py-1.5 text-sm text-slate-900 focus:border-indigo-500 focus:outline-none dark:border-slate-600 dark:bg-slate-800 dark:text-slate-100'

export function TierBenefitsPanel({
  tiers,
  benefitDefinitions,
  tierBenefits,
  onChanged,
}: {
  tiers: Tier[]
  benefitDefinitions: BenefitDefinition[]
  tierBenefits: TierBenefit[]
  onChanged: () => void
}) {
  const [tierId, setTierId] = useState<number | ''>(tiers[0]?.id ?? '')
  const [benefitId, setBenefitId] = useState<number | ''>(benefitDefinitions[0]?.id ?? '')
  const [configValue, setConfigValue] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      await adminApi.createTierBenefit(Number(tierId), Number(benefitId), configValue.trim())
      setConfigValue('')
      onChanged()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : String(err))
    } finally {
      setSubmitting(false)
    }
  }

  async function handleRowUpdate(row: TierBenefit, configValue: string, active: boolean) {
    setError(null)
    try {
      await adminApi.updateTierBenefit(row.id, configValue, active)
      onChanged()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : String(err))
    }
  }

  return (
    <div className="rounded-lg border border-slate-200 bg-white p-5 dark:border-slate-700 dark:bg-slate-800">
      <h3 className="mb-3 text-lg font-semibold text-slate-900 dark:text-slate-100">Tier benefits</h3>
      <p className="mb-4 text-sm text-slate-500 dark:text-slate-400">
        Which benefits each tier grants, and their config (e.g. discount percentage).
      </p>

      <form onSubmit={handleSubmit} className="mb-4 flex flex-wrap items-end gap-2">
        <div>
          <label className="block text-xs font-medium text-slate-600 dark:text-slate-400">Tier</label>
          <select value={tierId} onChange={(e) => setTierId(Number(e.target.value))} className={inputClass}>
            {tiers.map((tier) => (
              <option key={tier.id} value={tier.id}>
                {tier.code}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label className="block text-xs font-medium text-slate-600 dark:text-slate-400">Benefit</label>
          <select value={benefitId} onChange={(e) => setBenefitId(Number(e.target.value))} className={inputClass}>
            {benefitDefinitions.map((def) => (
              <option key={def.id} value={def.id}>
                {def.code}
              </option>
            ))}
          </select>
        </div>
        <div>
          <label className="block text-xs font-medium text-slate-600 dark:text-slate-400">Config value</label>
          <input
            value={configValue}
            onChange={(e) => setConfigValue(e.target.value)}
            placeholder="e.g. 10 for 10%"
            className={inputClass}
          />
        </div>
        <button
          type="submit"
          disabled={submitting || !tierId || !benefitId}
          className="rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold text-white transition hover:bg-indigo-500 disabled:opacity-50"
        >
          Assign
        </button>
      </form>

      {error && <p className="mb-3 text-sm text-red-600 dark:text-red-400">{error}</p>}

      <table className="w-full text-left text-sm">
        <thead>
          <tr className="text-xs font-semibold uppercase text-slate-500 dark:text-slate-400">
            <th className="py-1.5 pr-4">Tier</th>
            <th className="py-1.5 pr-4">Benefit</th>
            <th className="py-1.5 pr-4">Config</th>
            <th className="py-1.5 pr-4">Active</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-100 dark:divide-slate-700">
          {tierBenefits.map((row) => (
            <TierBenefitRow key={row.id} row={row} onSave={handleRowUpdate} />
          ))}
        </tbody>
      </table>
    </div>
  )
}

function TierBenefitRow({
  row,
  onSave,
}: {
  row: TierBenefit
  onSave: (row: TierBenefit, configValue: string, active: boolean) => void
}) {
  const [configValue, setConfigValue] = useState(row.configValue ?? '')

  return (
    <tr>
      <td className="py-1.5 pr-4 font-medium text-slate-900 dark:text-slate-100">{row.tierCode}</td>
      <td className="py-1.5 pr-4 text-slate-700 dark:text-slate-300">{row.benefitCode}</td>
      <td className="py-1.5 pr-4">
        <input
          value={configValue}
          onChange={(e) => setConfigValue(e.target.value)}
          onBlur={() => configValue !== (row.configValue ?? '') && onSave(row, configValue, row.active)}
          className={`${inputClass} w-28`}
        />
      </td>
      <td className="py-1.5 pr-4">
        <input
          type="checkbox"
          checked={row.active}
          onChange={(e) => onSave(row, configValue, e.target.checked)}
          className="h-4 w-4 accent-indigo-600"
        />
      </td>
    </tr>
  )
}
