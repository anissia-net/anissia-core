package anissia.domain.board.service

interface GetTopicRecentForHome {
    fun handle(): Map<String, List<Map<String, Any>>>
}
