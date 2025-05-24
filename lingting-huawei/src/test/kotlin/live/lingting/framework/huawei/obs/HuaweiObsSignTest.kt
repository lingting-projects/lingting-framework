package live.lingting.framework.huawei.obs

import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.huawei.HuaweiObs
import live.lingting.framework.huawei.HuaweiUtils
import live.lingting.framework.value.multi.StringMultiValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import java.time.LocalDate

/**
 * @author lingting 2024/11/5 13:53
 */
@EnabledIfSystemProperty(named = "framework.huawei.obs.test", matches = "true")
class HuaweiObsSignTest {

    @Test
    fun test() {
        testHeaders()
        testParams()
    }

    fun testParams() {
        val headers = HttpHeaders.empty()
        headers.put(
            HuaweiObs.HEADER_TOKEN,
            "ggpjbi1ub3J0aC00Twh7ImFjY2VzcyI6IkhTVDNVOEM0MEtXTjdCRkw5UjgxIiwibWV0aG9kcyI6WyJ0b2tlbiJdLCJwb2xpY3kiOnsiVmVyc2lvbiI6IjEuMSIsIlN0YXRlbWVudCI6W3siQWN0aW9uIjpbIm9iczpvYmplY3Q6R2V0T2JqZWN0Iiwib2JzOm9iamVjdDpQdXRPYmplY3QiLCJvYnM6b2JqZWN0OkRlbGV0ZU9iamVjdCIsIm9iczpvYmplY3Q6R2V0T2JqZWN0QWNsIiwib2JzOm9iamVjdDpQdXRPYmplY3RBY2wiLCJvYnM6b2JqZWN0Ok1vZGlmeU9iamVjdE1ldGFEYXRhIiwib2JzOm9iamVjdDpHZXRPYmplY3RUYWdnaW5nIiwib2JzOm9iamVjdDpQdXRPYmplY3RUYWdnaW5nIiwib2JzOm9iamVjdDpEZWxldGVPYmplY3RUYWdnaW5nIiwib2JzOm9iamVjdDpMaXN0TXVsdGlwYXJ0VXBsb2FkUGFydHMiLCJvYnM6b2JqZWN0OkFib3J0TXVsdGlwYXJ0VXBsb2FkIiwib2JzOmJ1Y2tldDpMaXN0QnVja2V0Iiwib2JzOmJ1Y2tldDpQdXRCdWNrZXRBY2wiLCJvYnM6YnVja2V0OkdldEJ1Y2tldFRhZ2dpbmciLCJvYnM6YnVja2V0OlB1dEJ1Y2tldFRhZ2dpbmciLCJvYnM6YnVja2V0OkRlbGV0ZUJ1Y2tldFRhZ2dpbmciLCJvYnM6YnVja2V0Okxpc3RCdWNrZXRNdWx0aXBhcnRVcGxvYWRzIl0sIlJlc291cmNlIjpbIm9iczoqOio6b2JqZWN0OmNsb3VkLXBob25lLWNuLWd6L3Rlc3QvMTkyNjIyNzUzMjEyMjgxNjUxMiJdLCJFZmZlY3QiOiJBbGxvdyJ9XX0sInJvbGUiOltdLCJyb2xldGFnZXMiOltdLCJ0aW1lb3V0X2F0IjoxNzQ4MTY5ODA1OTYxLCJ1c2VyIjp7ImRvbWFpbiI6eyJpZCI6IjRlOTgwMzFjMjY4NzQwMDdhZGM5MDgyMmY3MWU5MjE5IiwibmFtZSI6Im1vcHBvbW9iaV9pbmRvbmVzaWEiLCJ4ZG9tYWluX2lkIjoiMTAwNjIwMDAwMjIzNjk1MDgiLCJ4ZG9tYWluX3R5cGUiOiJIV0NfSEsifSwiaWQiOiI2YWQzZTczOWNkZTQ0NzExYWE2MGRjZTM1MGUzMTQ2MCIsIm5hbWUiOiJjbG91ZC1waG9uZS1zdHMtdXNlciIsInBhc3N3b3JkX2V4cGlyZXNfYXQiOiIiLCJ1c2VyX3R5cGUiOjE2fX0NjLFf_QUkIuN3h0ZPNEXk9Ti9BpehUNPdsrhVkqO8IIoa0t8MslP078A2vtUCrtsTJ2Xi9GxsSEw0iDYXmAWuEgdUxtkCwPEiqpqDSirYH0-QC9SQkWIhY-bTiwsLbjNeoDzbfaoI9jKdaKp2B-JCoqcad8-H2v7c95Ae3RacVikv9yjpzI-XmDBdOfoJzUqiQCAgR2AscnoW0l_hKnzPpIqSLxrls9SEtyA9oGujbN9RE05OSO1kccROJJ5Y-pTmQ7tZNWuC-_DqDGvTLqwp8FmpJhUzvSRZF7qOB7QOxPoi5PaA5sAaOLtR6ceYWjecRVZ_RvaA1_sx64-h9Iar"
        )
        val params = StringMultiValue()
        params.add("uploads")
        val signer = HuaweiObsSigner(
            HttpMethod.GET,
            "/bucket/",
            headers,
            null,
            params,
            "HST3U8C40KWN7BFL9R81",
            "HpBgWEZWpvar1Kw4C1CU1nCqLGqpY6f37scq6CZi"
        )

        val time = LocalDate.of(2025, 5, 24).atStartOfDay()
        val signed = signer.signed(time, true)


        assertEquals(
            "uploads&x-obs-security-token=ggpjbi1ub3J0aC00Twh7ImFjY2VzcyI6IkhTVDNVOEM0MEtXTjdCRkw5UjgxIiwibWV0aG9kcyI6WyJ0b2tlbiJdLCJwb2xpY3kiOnsiVmVyc2lvbiI6IjEuMSIsIlN0YXRlbWVudCI6W3siQWN0aW9uIjpbIm9iczpvYmplY3Q6R2V0T2JqZWN0Iiwib2JzOm9iamVjdDpQdXRPYmplY3QiLCJvYnM6b2JqZWN0OkRlbGV0ZU9iamVjdCIsIm9iczpvYmplY3Q6R2V0T2JqZWN0QWNsIiwib2JzOm9iamVjdDpQdXRPYmplY3RBY2wiLCJvYnM6b2JqZWN0Ok1vZGlmeU9iamVjdE1ldGFEYXRhIiwib2JzOm9iamVjdDpHZXRPYmplY3RUYWdnaW5nIiwib2JzOm9iamVjdDpQdXRPYmplY3RUYWdnaW5nIiwib2JzOm9iamVjdDpEZWxldGVPYmplY3RUYWdnaW5nIiwib2JzOm9iamVjdDpMaXN0TXVsdGlwYXJ0VXBsb2FkUGFydHMiLCJvYnM6b2JqZWN0OkFib3J0TXVsdGlwYXJ0VXBsb2FkIiwib2JzOmJ1Y2tldDpMaXN0QnVja2V0Iiwib2JzOmJ1Y2tldDpQdXRCdWNrZXRBY2wiLCJvYnM6YnVja2V0OkdldEJ1Y2tldFRhZ2dpbmciLCJvYnM6YnVja2V0OlB1dEJ1Y2tldFRhZ2dpbmciLCJvYnM6YnVja2V0OkRlbGV0ZUJ1Y2tldFRhZ2dpbmciLCJvYnM6YnVja2V0Okxpc3RCdWNrZXRNdWx0aXBhcnRVcGxvYWRzIl0sIlJlc291cmNlIjpbIm9iczoqOio6b2JqZWN0OmNsb3VkLXBob25lLWNuLWd6L3Rlc3QvMTkyNjIyNzUzMjEyMjgxNjUxMiJdLCJFZmZlY3QiOiJBbGxvdyJ9XX0sInJvbGUiOltdLCJyb2xldGFnZXMiOltdLCJ0aW1lb3V0X2F0IjoxNzQ4MTY5ODA1OTYxLCJ1c2VyIjp7ImRvbWFpbiI6eyJpZCI6IjRlOTgwMzFjMjY4NzQwMDdhZGM5MDgyMmY3MWU5MjE5IiwibmFtZSI6Im1vcHBvbW9iaV9pbmRvbmVzaWEiLCJ4ZG9tYWluX2lkIjoiMTAwNjIwMDAwMjIzNjk1MDgiLCJ4ZG9tYWluX3R5cGUiOiJIV0NfSEsifSwiaWQiOiI2YWQzZTczOWNkZTQ0NzExYWE2MGRjZTM1MGUzMTQ2MCIsIm5hbWUiOiJjbG91ZC1waG9uZS1zdHMtdXNlciIsInBhc3N3b3JkX2V4cGlyZXNfYXQiOiIiLCJ1c2VyX3R5cGUiOjE2fX0NjLFf_QUkIuN3h0ZPNEXk9Ti9BpehUNPdsrhVkqO8IIoa0t8MslP078A2vtUCrtsTJ2Xi9GxsSEw0iDYXmAWuEgdUxtkCwPEiqpqDSirYH0-QC9SQkWIhY-bTiwsLbjNeoDzbfaoI9jKdaKp2B-JCoqcad8-H2v7c95Ae3RacVikv9yjpzI-XmDBdOfoJzUqiQCAgR2AscnoW0l_hKnzPpIqSLxrls9SEtyA9oGujbN9RE05OSO1kccROJJ5Y-pTmQ7tZNWuC-_DqDGvTLqwp8FmpJhUzvSRZF7qOB7QOxPoi5PaA5sAaOLtR6ceYWjecRVZ_RvaA1_sx64-h9Iar",
            signed.canonicalQuery
        )


        assertEquals(
            "/bucket/?uploads&x-obs-security-token=ggpjbi1ub3J0aC00Twh7ImFjY2VzcyI6IkhTVDNVOEM0MEtXTjdCRkw5UjgxIiwibWV0aG9kcyI6WyJ0b2tlbiJdLCJwb2xpY3kiOnsiVmVyc2lvbiI6IjEuMSIsIlN0YXRlbWVudCI6W3siQWN0aW9uIjpbIm9iczpvYmplY3Q6R2V0T2JqZWN0Iiwib2JzOm9iamVjdDpQdXRPYmplY3QiLCJvYnM6b2JqZWN0OkRlbGV0ZU9iamVjdCIsIm9iczpvYmplY3Q6R2V0T2JqZWN0QWNsIiwib2JzOm9iamVjdDpQdXRPYmplY3RBY2wiLCJvYnM6b2JqZWN0Ok1vZGlmeU9iamVjdE1ldGFEYXRhIiwib2JzOm9iamVjdDpHZXRPYmplY3RUYWdnaW5nIiwib2JzOm9iamVjdDpQdXRPYmplY3RUYWdnaW5nIiwib2JzOm9iamVjdDpEZWxldGVPYmplY3RUYWdnaW5nIiwib2JzOm9iamVjdDpMaXN0TXVsdGlwYXJ0VXBsb2FkUGFydHMiLCJvYnM6b2JqZWN0OkFib3J0TXVsdGlwYXJ0VXBsb2FkIiwib2JzOmJ1Y2tldDpMaXN0QnVja2V0Iiwib2JzOmJ1Y2tldDpQdXRCdWNrZXRBY2wiLCJvYnM6YnVja2V0OkdldEJ1Y2tldFRhZ2dpbmciLCJvYnM6YnVja2V0OlB1dEJ1Y2tldFRhZ2dpbmciLCJvYnM6YnVja2V0OkRlbGV0ZUJ1Y2tldFRhZ2dpbmciLCJvYnM6YnVja2V0Okxpc3RCdWNrZXRNdWx0aXBhcnRVcGxvYWRzIl0sIlJlc291cmNlIjpbIm9iczoqOio6b2JqZWN0OmNsb3VkLXBob25lLWNuLWd6L3Rlc3QvMTkyNjIyNzUzMjEyMjgxNjUxMiJdLCJFZmZlY3QiOiJBbGxvdyJ9XX0sInJvbGUiOltdLCJyb2xldGFnZXMiOltdLCJ0aW1lb3V0X2F0IjoxNzQ4MTY5ODA1OTYxLCJ1c2VyIjp7ImRvbWFpbiI6eyJpZCI6IjRlOTgwMzFjMjY4NzQwMDdhZGM5MDgyMmY3MWU5MjE5IiwibmFtZSI6Im1vcHBvbW9iaV9pbmRvbmVzaWEiLCJ4ZG9tYWluX2lkIjoiMTAwNjIwMDAwMjIzNjk1MDgiLCJ4ZG9tYWluX3R5cGUiOiJIV0NfSEsifSwiaWQiOiI2YWQzZTczOWNkZTQ0NzExYWE2MGRjZTM1MGUzMTQ2MCIsIm5hbWUiOiJjbG91ZC1waG9uZS1zdHMtdXNlciIsInBhc3N3b3JkX2V4cGlyZXNfYXQiOiIiLCJ1c2VyX3R5cGUiOjE2fX0NjLFf_QUkIuN3h0ZPNEXk9Ti9BpehUNPdsrhVkqO8IIoa0t8MslP078A2vtUCrtsTJ2Xi9GxsSEw0iDYXmAWuEgdUxtkCwPEiqpqDSirYH0-QC9SQkWIhY-bTiwsLbjNeoDzbfaoI9jKdaKp2B-JCoqcad8-H2v7c95Ae3RacVikv9yjpzI-XmDBdOfoJzUqiQCAgR2AscnoW0l_hKnzPpIqSLxrls9SEtyA9oGujbN9RE05OSO1kccROJJ5Y-pTmQ7tZNWuC-_DqDGvTLqwp8FmpJhUzvSRZF7qOB7QOxPoi5PaA5sAaOLtR6ceYWjecRVZ_RvaA1_sx64-h9Iar",
            signed.canonicalizedResource
        )

        assertEquals(
            "GET\n" +
                    "\n" +
                    "\n" +
                    "1748016000\n" +
                    "/bucket/?uploads&x-obs-security-token=ggpjbi1ub3J0aC00Twh7ImFjY2VzcyI6IkhTVDNVOEM0MEtXTjdCRkw5UjgxIiwibWV0aG9kcyI6WyJ0b2tlbiJdLCJwb2xpY3kiOnsiVmVyc2lvbiI6IjEuMSIsIlN0YXRlbWVudCI6W3siQWN0aW9uIjpbIm9iczpvYmplY3Q6R2V0T2JqZWN0Iiwib2JzOm9iamVjdDpQdXRPYmplY3QiLCJvYnM6b2JqZWN0OkRlbGV0ZU9iamVjdCIsIm9iczpvYmplY3Q6R2V0T2JqZWN0QWNsIiwib2JzOm9iamVjdDpQdXRPYmplY3RBY2wiLCJvYnM6b2JqZWN0Ok1vZGlmeU9iamVjdE1ldGFEYXRhIiwib2JzOm9iamVjdDpHZXRPYmplY3RUYWdnaW5nIiwib2JzOm9iamVjdDpQdXRPYmplY3RUYWdnaW5nIiwib2JzOm9iamVjdDpEZWxldGVPYmplY3RUYWdnaW5nIiwib2JzOm9iamVjdDpMaXN0TXVsdGlwYXJ0VXBsb2FkUGFydHMiLCJvYnM6b2JqZWN0OkFib3J0TXVsdGlwYXJ0VXBsb2FkIiwib2JzOmJ1Y2tldDpMaXN0QnVja2V0Iiwib2JzOmJ1Y2tldDpQdXRCdWNrZXRBY2wiLCJvYnM6YnVja2V0OkdldEJ1Y2tldFRhZ2dpbmciLCJvYnM6YnVja2V0OlB1dEJ1Y2tldFRhZ2dpbmciLCJvYnM6YnVja2V0OkRlbGV0ZUJ1Y2tldFRhZ2dpbmciLCJvYnM6YnVja2V0Okxpc3RCdWNrZXRNdWx0aXBhcnRVcGxvYWRzIl0sIlJlc291cmNlIjpbIm9iczoqOio6b2JqZWN0OmNsb3VkLXBob25lLWNuLWd6L3Rlc3QvMTkyNjIyNzUzMjEyMjgxNjUxMiJdLCJFZmZlY3QiOiJBbGxvdyJ9XX0sInJvbGUiOltdLCJyb2xldGFnZXMiOltdLCJ0aW1lb3V0X2F0IjoxNzQ4MTY5ODA1OTYxLCJ1c2VyIjp7ImRvbWFpbiI6eyJpZCI6IjRlOTgwMzFjMjY4NzQwMDdhZGM5MDgyMmY3MWU5MjE5IiwibmFtZSI6Im1vcHBvbW9iaV9pbmRvbmVzaWEiLCJ4ZG9tYWluX2lkIjoiMTAwNjIwMDAwMjIzNjk1MDgiLCJ4ZG9tYWluX3R5cGUiOiJIV0NfSEsifSwiaWQiOiI2YWQzZTczOWNkZTQ0NzExYWE2MGRjZTM1MGUzMTQ2MCIsIm5hbWUiOiJjbG91ZC1waG9uZS1zdHMtdXNlciIsInBhc3N3b3JkX2V4cGlyZXNfYXQiOiIiLCJ1c2VyX3R5cGUiOjE2fX0NjLFf_QUkIuN3h0ZPNEXk9Ti9BpehUNPdsrhVkqO8IIoa0t8MslP078A2vtUCrtsTJ2Xi9GxsSEw0iDYXmAWuEgdUxtkCwPEiqpqDSirYH0-QC9SQkWIhY-bTiwsLbjNeoDzbfaoI9jKdaKp2B-JCoqcad8-H2v7c95Ae3RacVikv9yjpzI-XmDBdOfoJzUqiQCAgR2AscnoW0l_hKnzPpIqSLxrls9SEtyA9oGujbN9RE05OSO1kccROJJ5Y-pTmQ7tZNWuC-_DqDGvTLqwp8FmpJhUzvSRZF7qOB7QOxPoi5PaA5sAaOLtR6ceYWjecRVZ_RvaA1_sx64-h9Iar",
            signed.source
        )

        assertEquals("9TKjbn5h8hOTf3wetRPWZLYSVOs=", signed.sign)
    }

    fun testHeaders() {
        val headers = HttpHeaders.empty()
        headers.contentType("application/xml")
        val params = StringMultiValue()
        params.add("uploads")
        val signer = HuaweiObsSigner(
            HttpMethod.GET,
            "/bucket/",
            headers,
            null,
            params,
            "ak",
            "sk"
        )
        val dateTime = HuaweiUtils.parse("Tue, 5 Nov 2024 06:26:17 GMT")
        val signed = signer.signed(dateTime)

        assertEquals(
            """
                    GET

                    application/xml
                    Tue, 5 Nov 2024 06:26:17 GMT
                    /bucket/?uploads
                    """.trimIndent(), signed.source
        )
        assertEquals("OBS ak:Y4gIe5i9N4/x9lEtkU9UX6q2Bzw=", signed.authorization)
    }
}
