package anissia.domain.activePanel.command

import anissia.domain.account.Account
import anissia.domain.board.BoardPost

class AddDeletePostLogActivePanelCommand(
    val post: BoardPost,
    val account: Account?
)
