package br.com.source.model.database

import br.com.source.model.domain.LocalRepository
import org.dizitart.no2.Nitrite

//https://www.dizitart.org/nitrite-database/#how-to-install

class LocalRepositoryDatabase {
    private var repository = Nitrite.builder()
        .compressed()
        .filePath("/db/local_repository.db")
        .openOrCreate().getRepository(LocalRepository::class.java)

    fun save(localRepository: LocalRepository) {
        repository.insert(localRepository)
    }

    fun delete(localRepository: LocalRepository) {
        repository.remove(localRepository)
    }

    fun all(): List<LocalRepository> {
        return repository.find().map { it }.toList()
    }
}