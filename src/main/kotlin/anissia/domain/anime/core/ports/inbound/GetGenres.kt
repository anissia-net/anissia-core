package anissia.domain.anime.core.ports.inbound

interface GetGenres {
    fun handle(): List<String>
}
