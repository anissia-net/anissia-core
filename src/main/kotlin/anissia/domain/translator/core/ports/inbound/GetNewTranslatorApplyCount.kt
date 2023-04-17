package anissia.domain.translator.core.ports.inbound

import org.springframework.stereotype.Service

@Service
interface GetNewTranslatorApplyCount {
    fun handle(): Int
}
