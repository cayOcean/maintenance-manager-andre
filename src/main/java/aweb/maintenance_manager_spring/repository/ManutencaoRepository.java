package br.com.aweb.maintenance_manager_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.aweb.maintenance_manager_spring.model.Manutencao;

@Repository // Marca a interface como repositório Spring para a entidade Manutencao
public interface ManutencaoRepository extends JpaRepository<Manutencao, Long> {
    // Herdando métodos padrão do JpaRepository para CRUD (findAll, save, delete, findById, etc.)
}
