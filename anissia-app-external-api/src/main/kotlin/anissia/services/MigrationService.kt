package anissia.services

import org.springframework.stereotype.Service
import java.sql.ResultSet
import javax.sql.DataSource

@Service
class MigrationService(
    val dataSource: DataSource
) {
    fun migration() {
        account() // 계정
        anitime() // 애니메이션
        caption() // 자막
        bbs() // 공지
    }

    fun account() {
        query("""select * from oa.account order by an""") { e ->
            val an = e.getLong("an")
            val account = e.getString("account")
            val password = e.getString("password")
            val mail = e.getString("mail")
            val name = e.getString("name")
            val pms = e.getString("pms")
            val joind = e.getDate("joindate").toLocalDate()
            val lastd = e.getDate("lastdate").toLocalDate()

            println("$an $account $password $mail $name $pms $joind $lastd")
        }
    }

    fun anitime() {
        query("""
            select * from (
              select ai, subj, time, type, src, active, startdate, enddate, 'A' as gubun from oa.anitime union all
              select ai, subj, time, type, src, active, startdate, enddate, 'E' as gubun from oa.anitime_end
            ) a order by (case when a.startdate != '00000000' then startdate else enddate end)
            """) { e ->
            val animeNo = e.getLong("ai")
            val subject = e.getString("subj")
            val time = e.getString("time")
            val type = e.getString("type")
            val src = e.getString("src")
            val active = e.getString("active")
            val startdate = e.getString("startdate")
            val enddate = e.getString("enddate")
            val gubun = e.getString("gubun")

            println("$animeNo $subject $time $type $src $active $gubun $startdate $enddate")
        }
    }

    fun caption() {
        query("""select * from oa.anitime_cap order by ai, an""") { e ->
            val animeNo = e.getLong("ai")
            val an = e.getLong("an")
            val sharp = e.getString("sharp")
            val updt = e.getString("updt")
            val addr1 = e.getString("addr1")
            val addr2 = e.getString("addr2")

            println("$animeNo $an $sharp $updt $addr1 $addr2")
        }
    }

    fun bbs() {
        query("select * from oa.bbs where code = 1 and (bn in (4, 537, 659) or an = 942) order by bn") { e ->
            val boardNo = e.getLong("bn")
            val an = e.getLong("an")
            val subject = e.getString("subj")
            val text = e.getString("text")
            val date = e.getDate("date").toLocalDate()

            println("$boardNo $an $subject $text $date")
        }
    }

    fun query(sql: String, callback: (rs: ResultSet) -> Unit) {
        dataSource.connection.use {
            it.prepareStatement(sql).use {
                it.executeQuery().use {
                    while (it.next()) {
                        callback(it)
                    }
                }
            }
        }
    }
}

