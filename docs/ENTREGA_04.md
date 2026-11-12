# Cliquim Arquivos 0.4.0 — candidato para testes centralizados

12/11/2026. `com.studiocliquim.arquivos`, versionCode 4, minSdk 26, target/compileSdk 36. Assinatura debug. Não publicado nem aprovado para Play Store.

## Desenvolvimento concluído nesta etapa

Continuação da 0.3, preservando o trabalho local recebido. A desconexão do Mac foi recuperada; o checkpoint HANDOFF_04_WIP é histórico. Foram acrescentadas as funções aprovadas que podiam ser implementadas sem enfraquecer a proteção dos arquivos:

- Páginas dentro da moldura Explorer, com retorno à pasta e preservação da prévia ao fechar propriedades.
- Busca por nome, categoria MIME, data de modificação e tamanho mínimo. Subpastas opcionais, troca entre locais autorizados, limpar filtros, resultados com pasta de origem e ações para abrir pasta, favoritar e consultar propriedades.
- Leitura limitada, cancelável entre chamadas, com identificação explícita de cobertura parcial por erro, limite ou cancelamento. Sem índice de conteúdo inventado.
- Armazenamento: capacidade do volume local quando disponível e soma de arquivos acessíveis por categoria, incluindo quantidade sem tamanho informado. A soma das categorias não é apresentada como todo o espaço usado pelo sistema/apps.
- Home autorizada com categorias e análise; primeiro acesso continua oferecendo as permissões/seletor nativos Android.
- Favoritos de arquivos e pastas em lista selecionável, Abrir, Remover favorito e Adicionar. Atalhos sem acesso continuam disponíveis para remoção; remover atalho nunca exclui conteúdo.
- Propriedades em página Geral: nome, MIME, tamanho, dimensões de imagem lidas sem decodificar bitmap completo, caminho, modificação e capacidades reais.
- Preferências de tema, grade/lista, ocultos e pastas primeiro; ajuda organizada por tópico; tamanho do texto acompanha o Android.
- Destino de cópia/movimento em página navegável e confirmação de conflitos com metadados. Manter ambos ou pular nomes existentes. Substituir permanece indisponível.
- Histórico do último lote com estado por item, concluídos/pulados/atenção, origem/destino, cancelamento e indicação de operação interrompida. Registro privado local; não é fila que retoma tarefas.
- Checagem prévia de espaço local para arquivos de tamanho conhecido, sem excluir nada para liberar espaço. Falhas durante a cópia continuam registrando possíveis destinos parciais.
- Estado de acesso removido/volume desconectado centralizado, com reconectar/selecionar pasta ou voltar ao dispositivo. Favoritos preservados.

`FileEngine.java` não foi alterado. As estratégias existentes de cópia verificada e preservação de originais foram mantidas. A política de pular faz uma checagem antes de cada item; se outro aplicativo criar um conflito depois da checagem, o motor pode criar um nome único em vez de pular. Nunca substitui o conteúdo existente.

## Estado das 14 referências

| Prancha | Implementação para inspeção posterior |
|---|---|
| 01 navegação | Lista/grade, colunas responsivas, ícones, endereço e contagem reais. |
| 02 busca | Página com consulta/local/tipo/data/tamanho/subpastas, resultados e estado vazio. |
| 03 seleção/destino | Seleção acessível e barra de ações; página navegável de destino. |
| 04 criar/renomear | Diálogos próprios, validação inline e extensão preservada. Teclado/fonte grande ainda requerem inspeção. |
| 05 transferências | Página do último lote e estados reais. Sem porcentagem de bytes ou retomada inventada. |
| 06 conflito/exclusão | Manter ambos/pular; exclusão explicitamente permanente. Substituição desabilitada. |
| 07 prévia/propriedades | Prévia limitada e orientada por EXIF; propriedades em página Geral. |
| 08 acesso/desconexão | Home autorizada distinta do primeiro acesso; permissões nativas e recuperação de acesso. |
| 09 preferências/armazenamento | Preferências persistidas e análise real dos locais acessíveis. |
| 10 menus | Menus planos, checks, ícones e permissões/capacidades coerentes com a seleção. |
| 11 vazios/acesso removido | Estados distintos para pasta vazia, resultado vazio e local indisponível. |
| 12 espaço/substituição | Espaço local conhecido verificado; resultado explícito sem limpeza automática. Não há fluxo de substituição funcional. |
| 13 escuro/teclado | Tema escuro e formulários adaptáveis em código. Comparação deve usar exclusivamente a referência v2. |
| 14 favoritos/ajuda | Páginas próprias, favoritos de arquivos/pastas e ajuda por tópico. |

Esta tabela é um inventário do código, NÃO uma aprovação visual. Sem capturas novas não afirmamos reprodução exata das pranchas, bom comportamento em todas as larguras ou validação de acessibilidade.

## Evidência local desta versão

- `assembleDebug`, `assembleDebugAndroidTest`, `lintDebug`: BUILD SUCCESSFUL.
- Lint: 0 erros e 52 avisos (41 textos/localização; demais sobre journal síncrono, caminhos, ExifInterface framework, API preview, backup, alocação de desenho e espaço disponível).
- 60 checks JVM aprovados: FileEngineTest 21; ImageSizingTest 9; ReadOnlyScanTest 13; ScanRulesTest 17.
- Testes novos cobrem ciclo/duplicidade de referência, limite de entrada/profundidade/tempo, cancelamento, falha parcial do provedor, desconhecimento de metadados, categorias, critérios inclusivos e soma sem overflow.
- UiSmokeTest ampliado e compilado para páginas/retorno, favoritos de arquivo, política Pular sem alteração do original/destino e cancelamento de operação genérica pelo painel. Não executado.
- SHA-256 APK principal: `d56fd090bdce52e278aa30bfe2754a1981b7337b860cb005ddea59ba753b98c5`.

Nenhum simulador ou celular foi usado. Nenhuma instalação, desinstalação, captura Android, medição de RAM ou benchmark foi feita. Resultados antigos da 0.1/0.2 não validam esta versão.

## Limites mantidos deliberadamente

- Sem sobrescrever/substituição transacional, lixeira, undo, mover pastas, renomear SAF ou copiar pastas entre local e SAF. Essas funções exigem outra estratégia e validação de integridade antes de serem habilitadas.
- Sem serviço persistente de transferência ou retomada automática após encerramento. Histórico guarda detalhes dos últimos 100 itens do último lote; totais incluem o lote inteiro. Não interpreta um resultado incerto como sucesso.
- Busca/análise cobrem uma raiz autorizada por vez. Limites: 50 mil itens visitados, profundidade 64, orçamento de 60 s verificado entre chamadas; até mil resultados de busca. Chamadas de provedor e enumeração de uma pasta podem demorar mais; cancelamento é cooperativo e não promete interromper uma chamada bloqueada.
- Dados privados de outros apps permanecem inacessíveis. Metadados ausentes não viram zeros fictícios nos filtros. Capacidade SAF não é presumida.
- Sem recentes, índice persistente, miniaturas na lista, preview de vídeo/PDF, editor de imagem, sons retrô ou tamanho de fonte independente do Android.
- Cópias interrompidas podem conservar destinos parciais. Não há garantia de atomicidade contra edições concorrentes, desconexão ou falha física.

## Fila única de testes

Müller determinou um único Work, depois que todos os APKs estiverem preparados. Testar um app por vez, em várias versões Android, com fixtures próprias. Nenhuma instalação no telefone sem novo OK.

Pacote local em `task-6/deliverables/v0.4.0`: APK principal, APK engine-tests (runner SmokeTest), APK ui-tests (runner UiSmokeTest), hashes, build e lint. Instalar somente um APK de instrumentação por vez quando a fila for liberada.

Priorizar retorno entre páginas/preview, cancelamento ao sair/trocar filtros, permissões revogadas, fonte grande/teclado/rotação, temas, favoritos indisponíveis, provider lento, mídia com EXIF, muitas entradas, falta de espaço, conflitos e interrupção. Conferir registros e bytes reais das fixtures; não usar arquivos pessoais. Capturar os estados das 14 pranchas e medir RAM/performance. Depois da entrega, aguardar essa auditoria; alterações decorrentes dela exigem aprovação do usuário.
