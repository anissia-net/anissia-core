package anissia.domain.board.core.ports.inbound

interface GetTopicRecentForHome {
    fun handle(): Map<String, List<Map<String, Any>>>
}
