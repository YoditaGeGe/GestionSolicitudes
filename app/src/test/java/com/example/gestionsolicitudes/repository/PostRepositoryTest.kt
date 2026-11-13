package com.example.gestionsolicitudes.repository

import com.example.gestionsolicitudes.data.model.Post
import com.example.gestionsolicitudes.data.remote.ApiService
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

class TestablePostRepository(private val testApi: ApiService) : PostRepository() {
    override suspend fun getPosts(): List<Post> {
        return testApi.getPosts()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PostRepositoryTest : StringSpec({
    "getPosts() debe retornar una lista de posts simulada" {
        val fakePosts = listOf(
            Post(userId = 1, id = 1, title = "Titulo 1", body = "Cuerpo 1"),
            Post(userId = 2, id = 2, title = "Titulo 2", body = "Cuerpo 2")
        )

        val mockApi = mockk<ApiService>()
        coEvery { mockApi.getPosts() } returns fakePosts

        val repo = TestablePostRepository(testApi = mockApi)

        runTest {
            val resultado = repo.getPosts()
            resultado shouldContainExactly fakePosts
        }
    }
})
