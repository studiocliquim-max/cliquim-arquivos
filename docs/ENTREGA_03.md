# Cliquim Arquivos 0.3.0 — entrega para a fila de testes

12/11/2026. Package `com.studiocliquim.arquivos`, versionCode 3, minSdk 26, target/compileSdk 36. APK debug, não é release para Play Store.

## Continuidade

Continua `feat/retro-ui-v02` e PR #2, a partir de `70144520fbe1786fc2e40e2b08e2bf4a62abd411`. As seis alterações locais recebidas foram preservadas em backup antes da revisão. Nenhum reset, substituição pelo código da main ou edição concorrente.

O desenvolvimento foi preparado em uma cópia isolada para build. A sincronização de volta ao checkout exige comparação dos hashes de origem; alterações novas de outro Work interrompem a cópia. O motor FileEngine não foi alterado nesta versão.

## Código presente nesta versão

- Componentes recebidos: borda dupla por densidade, estados/foco, ícones próprios em grade de pixels, campo de endereço com seta separada, lista com colunas, indicadores de seleção e rodapé de ações.
- Menus planos com ações desabilitadas conforme seleção/provedor. O menu e o rodapé não oferecem mover pasta nem mover arquivo SAF como se fosse um movimento completo. Copiar continua disponível.
- Diálogos dimensionados pelo conteúdo, preferências persistentes, favoritos de pastas, propriedades reais e ajuda por tópicos.
- Prévia interna de imagens em leitura: decodificação fora da UI, limite de 2048 px por eixo, orientação EXIF quando reconhecida, descarte da imagem ao sair, falha sem alterar o arquivo, anterior/próxima, propriedades, abrir com e compartilhar. Prévia não é editor nem visualizador de PDF/vídeo.
- Corrigida atualização atrasada de pasta após voltar à home; invalidação de callbacks após destruir Activity; caminho de retorno persistido; ações de criação/busca suspensas enquanto a pasta está indisponível ou carregando.
- Tamanho de seleção marcado como parcial quando inclui pastas ou itens sem tamanho conhecido.

## Evidência executada no Mac

- `:app:assembleDebug :app:assembleDebugAndroidTest :app:lintDebug`: BUILD SUCCESSFUL.
- Lint: 0 erros, 23 avisos. Incluem textos/localização, caminhos de armazenamento, journal síncrono, ExifInterface do framework, API preview e desenho. Não foram tratados como aprovação de performance/acessibilidade.
- `FileEngineTest`: 21 verificações de nomes e conflitos passaram.
- `ImageSizingTest`: 9 verificações de limites inválidos, fotos grandes, panoramas e dimensões extremas passaram.
- APK principal SHA-256: `c4024d9d1b6bde323c4279fc0a9b692ab4c7612fe5fafa986d7e91a33212415c`.

Nenhuma instalação, desinstalação, interação ou teste em simulador/telefone nesta retomada. Nenhuma captura nova de UI. A 0.2 instalada anteriormente no Redmi não comprova a 0.3.

## Handoff para o Work único de testes

O usuário suspendeu testes individuais: executar posteriormente em fila, um aplicativo por vez e em várias versões Android. Não instalar no telefone sem novo OK. Manter dados pessoais fora das capturas e usar apenas fixtures descartáveis próprias.

Há dois APKs de instrumentação, instalados um por vez pelo responsável quando a fila for liberada:

- Engine: runner `com.studiocliquim.arquivos.SmokeTest`.
- UI: runner `com.studiocliquim.arquivos.UiSmokeTest`, compilado com `-PtestRunner=com.studiocliquim.arquivos.UiSmokeTest`.

O UiSmokeTest acrescenta PNG de fixture, abertura da prévia, dimensões, ações desabilitadas, retorno à pasta e uma listagem atrasada controlada por latch para comprovar que não substitui a home. Esses testes foram compilados, não executados. O teste usa armazenamento compartilhado; preparar autorização exclusivamente no emulador de QA antes da execução.

Conferir API 26 e versões atuais, largura pequena, fonte ampliada, rotação, teclado aberto, acessibilidade/TalkBack, imagens grandes/corrompidas/EXIF, leitura SAF, revogação, interrupção e memória durante troca de imagens. Gerar capturas reais para cada estado das 14 pranchas, identificando versão, commit, densidade e fonte. Usar exclusivamente `13-tema-escuro-teclado-v2.png` como referência 13.

## Pendências funcionais e visuais — não declarar concluído

As 14 pranchas NÃO estão reproduzidas integralmente. A auditoria de referência está em `design/pranchas-win98-2026-11-12`, `docs/design/win98-v1/AUDITORIA-VISUAL-02.md`.

| Grupo | Limite atual |
|---|---|
| Navegação/seleção/menus/criar | Implementados em código; medidas, toques, fontes e fidelidade aguardam execução visual. Destino ainda é diálogo navegável. |
| Busca | Nome/categoria só na pasta atual. Sem busca recursiva, filtros por data/tamanho nem painel completo da prancha. |
| Transferência | Resultado e progresso por item; sem painel de fila persistente, medição de bytes ou retomada. Manter app aberto. |
| Conflitos | Mantém ambos com nome único. Sem seletor pular/substituir e sem recuperação transacional de substituição. |
| Prévia/propriedades | Prévia implementada; propriedades ainda em diálogo, sem a página completa da prancha nem dimensões EXIF na ficha. |
| Home/acesso/armazenamento | Acesso nativo e favoritos; faltam categorias de home e análise/capacidade por volume. Desconexão usa erro geral. |
| Favoritos/ajuda/preferências | Conteúdo real em diálogos; favoritos somente de pastas. Falta composição de página completa e favoritos de arquivos. |
| Espaço/erro/escuro | Sem tela especializada de falta de espaço; tema e vazios no código aguardam conferência visual. |

Mover pastas, renomear SAF, copiar pastas entre local e SAF, sobrescrever, lixeira e undo continuam indisponíveis. Cópias interrompidas podem deixar destino parcial; nenhuma limpeza destrutiva automática. Mesmo após SHA-256 não há garantia contra alterações externas concorrentes ou falha física.

O APK está preparado para testes, não aprovado para distribuição comercial. Depois desta entrega, aguardar a auditoria centralizada e aprovação de novas alterações.
