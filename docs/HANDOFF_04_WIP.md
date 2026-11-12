> **Atualização:** conexão recuperada; continuação da 0.4 compilada e preparada para a fila centralizada de testes. O estado atual está em [ENTREGA_04.md](ENTREGA_04.md). O relato abaixo é o checkpoint histórico da desconexão.

# Handoff 0.4 — desenvolvimento local, ainda sem build

12/11/2026. O Mac ficou indisponível durante a continuação autorizada. Este documento preserva o ponto de retomada; não representa entrega de APK 0.4 nem aprovação de funcionalidades.

## Última versão verificável

Commit de código `c9410e7e041ae82d5e670989b92fb7f9c4b977be`, APK 0.3.0, SHA-256 `c4024d9d1b6bde323c4279fc0a9b692ab4c7612fe5fafa986d7e91a33212415c`. Build/lint e 30 checks puros passaram. Testes Android suspensos por determinação do usuário. Nenhum simulador ou celular usado.

## Arquivos no Mac

- Checkout original: `/Users/mulleralmeidasantos/Documents/Codex/2026-11-12/cliquim-arquivos`, código ainda em c9410e7.
- Desenvolvimento 0.4 em cópia isolada: `/Users/mulleralmeidasantos/Documents/Codex/2026-11-12/task-6/review-v04`.
- Baseline dos hashes de origem: `/Users/mulleralmeidasantos/Documents/Codex/2026-11-12/task-6/sync-baseline-v04.json`.
- Não substituir a cópia 0.4 pela main nem pelo checkout 0.3. Antes de sincronizar, conferir os hashes e possíveis edições concorrentes. A cópia não possui `.git`.

## Alterações gravadas antes da queda

VersionCode 4 / versionName 0.4.0; MainActivity integrado às páginas e ao registro de transferências; RetroPages refeito com páginas completas de propriedades, preferências, favoritos de arquivos/pastas e ajuda.

Novos arquivos em `app/src/main/java/com/studiocliquim/arquivos/`:

- `RetroScreen.java`: páginas dentro da moldura Explorer, retorno e descarte/cancelamento de leituras.
- `ReadOnlyScan.java`: travessia somente leitura, limitada a quantidade, profundidade e tempo, com indicação de cancelamento/cobertura parcial.
- `ScanRules.java`: regras puras de nome/tipo/data/tamanho e soma protegida contra overflow.
- `RetroCatalog.java`: formulário de busca com filtros e subpastas; resultados com local; armazenamento real por categoria e capacidade do volume quando disponível.
- `RetroHome.java`: home autorizada, atalhos por categoria, capacidade real e entrada para análise; seletor nativo para acesso restrito.
- `TransferLedger.java`: histórico do último lote com estados reais por item, limitado a 100 registros detalhados; sem retomada automática.
- `RetroTransfers.java`: página de destino navegável, confirmação de conflito manter ambos/pular, resultado por item, cancelamento e acesso à origem/destino.

FileEngine não foi alterado. Substituição, mover pastas, renomear SAF e fila durável continuam indisponíveis. A transferência acrescenta checagem de espaço local quando o tamanho é conhecido; precisa de revisão e testes antes de uso com dados reais.

## Próximos passos de código, ainda NÃO executados

A última chamada foi recusada por `exec-server transport disconnected` antes de criar o processo. Uma tentativa de leitura falhou após 45 s, e o ambiente passou a `unavailable`. Não foi possível compilar a 0.4. Não é rejeição de aprovação ou falta de autorização.

Revisar e aplicar o pequeno lote que ficou pendente:

1. Em `MainActivity.navigate`, quando o destino é arquivo, reconstruir a listagem antes de abrir o arquivo; usar a prévia interna se for imagem. Hoje a página pode ficar vazia ao abrir arquivo através dos locais.
2. Acrescentar `Armazenamento…` ao menu Exibir com ação e habilitação coerentes para a pasta atual.
3. Em `RetroPreview`, se a imagem não pertence à listagem atual, limpar a coleção anterior antes de adicionar a imagem; evitar misturar fotos de outra pasta ao abrir um favorito/resultado de busca.
4. Em `RetroTransfers.confirm`, esclarecer que a checagem de conflito é refeita antes de cada item e que uma criação concorrente por outro app pode resultar em nome único. Não prometer política de pular atomicamente: FileEngine mantém ambos se o conflito surgir depois da checagem.
5. Revisar estados de páginas/preview, troca de local durante leitura, retorno, cancelamento e ações indisponíveis. Revisar cores de Spinners no tema escuro, formulários em fonte grande e estados de acesso removido.
6. Criar testes puros para ReadOnlyScan/ScanRules (ciclo, erro parcial, limite, cancelamento, filtros sem metadados e overflow) e ampliar a instrumentação, somente COMPILANDO os APKs de teste agora.
7. Compilar com JDK 17 e SDK 36; corrigir erros/lint; documentar o estado real das 14 pranchas. Não usar resultados da 0.3 como evidência da 0.4.
8. Sincronizar as mudanças preservadas e gerar novo APK/hash. Aguardar o Work único de testes, sem usar simulador ou celular.

JDK: `/Users/mulleralmeidasantos/Documents/Codex/2026-11-11/deixa-eu-te-falar-eu-t/work/tooling/jdk-17.0.21+9/Contents/Home`. SDK: `/Users/mulleralmeidasantos/Library/Android/sdk`.

As mudanças 0.4 listadas acima existem apenas na cópia local até que o próximo responsável consiga recuperá-la, revisar e sincronizar. Este commit envia somente o handoff.

