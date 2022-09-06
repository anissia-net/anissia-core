package anissia.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LegacyController() {

    @RequestMapping("/anitime/list_img", produces = ["image/svg+xml;charset=utf-8"])
    fun svg(): String =
"""<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
<svg version="1.1" id="Layer_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px"
     width="170px" height="250px" viewBox="0 0 170 250" enable-background="new 0 0 170 250" xml:space="preserve">
<rect fill="#555" width="100%" height="100%"/>
<rect fill="#333" width="100%" height="30"/>
<text x="0" y="0" fill="#fff" font-family="'맑은고딕'" font-size="13" text-anchor="middle">
<tspan x="50%" dy="21" font-weight="bold">애니 편성표</tspan>
<tspan x="50%" dy="46">애니편성표가</tspan>
<tspan x="50%" dy="20">업데이트 되었습니다.</tspan>
<tspan x="50%" dy="20">애니시아</tspan>
<tspan x="50%" dy="20">[ www.anissia.net ]</tspan>
<tspan x="50%" dy="20">를 방문하셔서</tspan>
<tspan x="50%" dy="20">메뉴 [애니 편성표] 클릭</tspan>
<tspan x="50%" dy="20">이미지 [블로그] 선택</tspan>
<tspan x="50%" dy="20">업데이트 해주세요!</tspan>
</text>
</svg>"""

    @RequestMapping("/anitime/list")
    fun list() = """[{"i":0,"s":"애니시아 API 주소 이전","t":"0000","g":"공지","l":"https://anissia.net","a":true,"sd":"00000000","ed":"00000000"}]"""

    @RequestMapping("/anitime/cap")
    fun cap() = """[{"s":"00010","d":"20210208000000","a":"https://anissia.net","n":"애니시아"}]"""

    @RequestMapping("/anitime/list.js")
    fun listJs() = "anitimeJson(${list()});"

    @RequestMapping("/anitime/cap.js")
    fun capJs() = "anitimeJson(${cap()});"

}