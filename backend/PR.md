# Implementa criação e ativação de fichas de treino em camadas

Permite selecionar um aluno e exercícios do catálogo, organizar divisões e definir séries, faixa de repetições, carga inicial e descanso. Ao salvar e ativar, a ficha anterior é arquivada e a nova fica disponível para consulta no perfil.

## Decisão arquitetural

O fluxo segue Controller → Service → Repository, conforme a Aula 09. Controllers traduzem HTTP; serviços concentram regras e transações; repositórios isolam o acesso ao H2. DTOs validam os contratos e o domínio representa as fichas. O diagrama está no README.

O bloqueio pessimista por aluno serializa ativações concorrentes. O arquivamento e a criação são atômicos. As prescrições são snapshots JSON para preservar parâmetros históricos.

## Validação

`mvn -o test`: testes de integração com H2 em memória, incluindo ativação, histórico, rejeições e contrato HTTP.

## Escopo

API de backend. O formulário e o perfil visual devem consumir os endpoints documentados. Autenticação e autorização ficam para outra entrega. O perfil demo permite experimentar com alunos e exercícios iniciais.
