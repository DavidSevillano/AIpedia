package com.burixer85.aipedia.core.data.repository

import com.burixer85.aipedia.core.data.remote.AiComparisonDto
import com.burixer85.aipedia.core.domain.model.AiSpec
import com.burixer85.aipedia.core.domain.model.AiUseCase
import com.burixer85.aipedia.core.domain.model.ComparisonData
import com.burixer85.aipedia.core.domain.model.Integration
import com.burixer85.aipedia.core.domain.model.PricingPlan
import com.burixer85.aipedia.core.domain.model.ProCon
import com.burixer85.aipedia.core.domain.repository.ComparisonRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import javax.inject.Inject

class ComparisonRepositoryImp @Inject constructor(
    private val supabase: SupabaseClient
) : ComparisonRepository {

    override suspend fun getComparisonData(aiId: String): ComparisonData {
        val dto = supabase.from("ai")
            .select(
                columns = Columns.raw(
                    "id, ai_specs(*), pricing_plans(*), ai_pros_cons(*), integrations(*), use_cases(*)"
                )
            ) {
                filter { eq("id", aiId) }
            }
            .decodeSingleOrNull<AiComparisonDto>() ?: return ComparisonData()

        return ComparisonData(
            specs = dto.specs.sortedBy { it.sortOrder }.map {
                AiSpec(it.labelEs, it.labelEn, it.valueEs, it.valueEn)
            },
            plans = dto.plans.sortedBy { it.sortOrder }.map {
                PricingPlan(
                    nameEs = it.nameEs,
                    nameEn = it.nameEn,
                    priceMonthly = it.priceMonthly,
                    priceYearly = it.priceYearly,
                    billingNoteEs = it.billingNoteEs,
                    billingNoteEn = it.billingNoteEn,
                    includesEs = it.includesEs,
                    includesEn = it.includesEn,
                )
            },
            pros = dto.prosCons.filter { it.kind == "pro" }.sortedBy { it.sortOrder }
                .map { ProCon(isPro = true, textEs = it.textEs, textEn = it.textEn) },
            cons = dto.prosCons.filter { it.kind == "con" }.sortedBy { it.sortOrder }
                .map { ProCon(isPro = false, textEs = it.textEs, textEn = it.textEn) },
            integrations = dto.integrations.map { Integration(it.name, it.iconUrl) },
            useCases = dto.useCases.map { AiUseCase(it.nameEs, it.nameEn) },
        )
    }
}
