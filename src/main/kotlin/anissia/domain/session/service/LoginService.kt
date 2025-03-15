package anissia.domain.session.service

import anissia.domain.session.command.DoTokenLoginCommand
import anissia.domain.session.command.DoUserLoginCommand
import anissia.domain.session.model.JwtAuthInfoItem
import anissia.domain.session.model.SessionItem
import anissia.shared.ResultWrapper

interface LoginService {
    fun doUserLogin(cmd: DoUserLoginCommand, sessionItem: SessionItem): ResultWrapper<JwtAuthInfoItem>
    fun doTokenLogin(cmd: DoTokenLoginCommand, sessionItem: SessionItem): ResultWrapper<JwtAuthInfoItem>
    fun updateAuthInfo(sessionItem: SessionItem): ResultWrapper<JwtAuthInfoItem>
}
