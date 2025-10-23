# src/test/resources/features/bookstore.feature
Feature: Gerenciamento de Livros na DemoQA Bookstore API

  Como um usuário de testes
  Eu desejo interagir com a API da BookStore
  Para criar um usuário, alugar livros e visualizar a reserva

  Background: Configuração inicial da API
    * a base URI da API é "https://demoqa.com"

  @Desafio
  Scenario: Fluxo completo de criação de usuário e reserva de livros
    Given que um novo usuário com nome "usuario1TesteAPI" e senha "Senha@123" será criado
    When eu realizar a requisição POST para criar o usuário
    Then a resposta deve ter o status 201 e o ID do usuário deve ser capturado

    # Geração de Token
    Given que o usuário e a senha capturados serão usados para gerar um token
    When eu realizar a requisição POST para gerar o token de acesso
    Then a resposta deve ter o status 200 e o token deve ser capturado

    # Autorização
    When eu realizar a requisição POST para verificar a autorização do usuário
    Then a resposta deve ter o status 200 e o corpo deve indicar "true"

    # Listar Livros
    When eu realizar a requisição GET para listar todos os livros
    Then a resposta deve ter o status 200 e dois ISBNs de livros devem ser capturados

    # Reservar Livros
    Given que os dois ISBNs de livros e o User ID capturados serão usados para reservar
    When eu realizar a requisição POST para adicionar os livros ao usuário
    Then a resposta deve ter o status 201 e a lista de livros deve ser validada

    # Listar Detalhes do Usuário (com Livros Reservados)
    Given que o User ID capturado será usado para listar os detalhes
    When eu realizar a requisição GET para listar os detalhes do usuário
    Then a resposta deve ter o status 200 e a lista de livros do usuário deve conter os dois livros reservados