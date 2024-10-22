package anissia.domain.translator.service

import org.springframework.stereotype.Service

@Service
interface GetNewTranslatorApplyCount {
    fun handle(): Int
}
