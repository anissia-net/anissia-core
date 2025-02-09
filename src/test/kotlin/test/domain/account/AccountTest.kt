package test.domain.account

class AccountTest(
) {
//    @PostMapping("/register")
//    fun register(@RequestBody cmd: RegisterCommand, exchange: ServerWebExchange) =
//        register.handle(cmd, exchange.sessionItem)
//
//    @PutMapping("/register")
//    fun registerValidation(@RequestBody cmd: RegisterValidationCommand, exchange: ServerWebExchange) =
//        registerValidation.handle(cmd)
//
//    @PostMapping("/recover")
//    fun recover(@RequestBody cmd: RecoverCommand, exchange: ServerWebExchange) =
//        recover.handle(cmd, exchange.sessionItem)
//
//    @PutMapping("/recover")
//    fun recoverValidation(@RequestBody cmd: RecoverValidationCommand, exchange: ServerWebExchange) =
//        recoverValidation.handle(cmd)
//
//    @PutMapping("/recover/password")
//    fun recoverPassword(@RequestBody cmd: RecoverPasswordCommand, exchange: ServerWebExchange) =
//        recoverPassword.handle(cmd)

//    @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = [ForumInfoRepository::class])
//    @ExtendWith(MockitoExtension::class)
//    class MokitoExampleTest(
//        @Mock private val forumInfoRepository: ForumInfoRepository
//    ) {
//        @Test
//        fun getForumInfo( ) {
//            // findAll의 값 주입
//            `when`(forumInfoRepository.findAll()).thenReturn(listOf(
//                ForumInfo.create(
//                    forumId = "aaa",
//                    name = "ccc",
//                    caption = "aaa",
//                )))
//
//            val getForumInfo = GetForumInfoService(forumInfoRepository)
//            getForumInfo.cache()
//
//            println(forumInfoRepository.findAll())
//
//            val forumId = "aaa"
//            val forumInfo = getForumInfo.handle(forumId)
//            println(forumInfo)
//        }
//    }

//    @SpringBootTest(classes = [Application::class])
//    @AutoConfigureWebTestClient
//    class ForumTest(
//        @Autowired private val webClient: WebTestClient
//    ) {
//        @Test
//        fun getForumInfo( ) {
////        println("포럼 테스트 준비")
////        println(forumController.getForumInfo())
////        println("포럼 테스트 완료")
//
//            println("준비!!!")
//
//            var a = webClient.get()
//                .uri("/forum/info")
//                .exchange()
//                .expectStatus().isOk
//                .expectBody()
//                .returnResult()
//
//
//            println(a)
//            println("완료!!!")
//        }
//    }


}
