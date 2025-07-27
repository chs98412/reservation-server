package kr.hhplus.be.server.domain.point

import jakarta.persistence.*
import kr.hhplus.be.server.common.exception.InsufficientBalanceException
import java.time.LocalDateTime

@Entity
@Table(name = "balance")
class Balance(

    @Id
    @Column(name = "balance_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val balanceId: Long,

    @Column(name = "account_id")
    val accountId: String,

    amount: Long,
) {
    @Column(name = "amount")
    var amount = amount
        private set

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()

    fun charge(amount: Long) {
        this.amount += amount
    }

    fun deduct(price: Long) {
        if (amount < price) throw InsufficientBalanceException()
        amount -= price
    }
}