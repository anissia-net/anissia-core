package anissia.domain.activePanel.command

import anissia.domain.account.Account
import anissia.domain.board.BoardPost
import anissia.domain.board.BoardTopic

class AddDeleteTopicLogActivePanelCommand(
    val topic: BoardTopic,
    val post: BoardPost,
    val account: Account?
)
