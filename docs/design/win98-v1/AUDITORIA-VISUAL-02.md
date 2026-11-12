# Auditoria visual — pranchas × APK 0.2.0
Data: 12/11/2026. Responsável: Work das pranchas.

## Direção atual do Müller
Pedido vigente: abrir no Android, conferir tela por tela e fazer o APK reproduzir o nível de detalhe das pranchas. A referência visual do conjunto está autorizada para implementação; a formulação antiga de “telas adicionais aguardando aprovação” não deve impedir esse trabalho. Uma interface apenas cinza e azul não atende ao pedido.

## Evidências e limites
- Código inspecionado: [feat/retro-ui-v02, commit 70144520](https://github.com/studiocliquim-max/cliquim-arquivos/tree/70144520fbe1786fc2e40e2b08e2bf4a62abd411).
- Lidos MainActivity.java, RetroUi.java, RetroDialog.java e docs/INTEGRACAO_UI_02.md.
- Inspecionadas visualmente as seis capturas salvas em [docs/screenshots/v0.2](https://github.com/studiocliquim-max/cliquim-arquivos/tree/70144520fbe1786fc2e40e2b08e2bf4a62abd411/docs/screenshots/v0.2): home, menu, new-folder, dark, selection e grid.
- Essas capturas são anteriores aos dois últimos ajustes cosméticos. A integração registra a instalação posterior no Redmi com dados preservados, mas sem o ensaio final completo.
- Neste Work, a tentativa atual de adb devices -l retornou “No usable execution environment is available”. Não houve inspeção ao vivo, teste, compilação ou instalação nesta revisão.
- A captura chamada ui-grid.png mostra lista em seleção; ui-selection.png mostra lista sem seleção. Não usar esses nomes como evidência de grade/seleção corretas. Gerar novamente com estado verificável.
- Esta entrega é auditoria, não implementação. Nenhuma mudança no código do outro Work.

## Diferenças comuns verificadas
1. **Bordas/relevo:** Frame desenha apenas uma linha clara e uma escura, dimensionadas pela largura do componente; por isso falta o relevo duplo das pranchas. Centralizar as espessuras por densidade e estado, com borda externa escura, intermediária cinza e luz interna. Definir pressionado, foco pontilhado, selecionado e desabilitado.
2. **Ícones:** Symbol usa desenhos vetoriais muito simples. Acima é só uma seta, em vez de pasta + seta; áudio cai no documento genérico; faltam computador do endereço, folha dobrada, miniatura, filme, lixeira, nova pasta, seleção, alça da barra e vários símbolos de menu. Criar desenhos próprios em grade de pixels com contorno, luz e sombra. Não extrair a tela inteira como fundo.
3. **Rodapé:** os três botões têm só texto. Adicionar ícones + texto e divisórias, mesma família visual do restante. No modo seleção, trocar para as ações da seleção.
4. **Menus:** menuPopup usa Button com relevo em todas as opções. As pranchas pedem linhas planas, coluna de ícones/checks, separadores por grupo e destaque azul somente no item ativo; o relevo pertence ao contorno do menu.
5. **Endereço:** seta é parte do texto e quebra para uma segunda linha nas capturas. Separar ícone, caminho com elipse e botão de seta; caminho completo acessível. Campo deve manter composição ao navegar para nomes longos.
6. **Lista:** render cria nome e detalhe empilhados. Faltam cabeçalhos Nome/Tipo/Tamanho, alinhamento vertical das colunas e foco/seleção como na prancha. Em tela estreita ou fonte ampliada, preservar nome e ações, permitindo adaptação controlada.
7. **Tipografia/espaçamento:** usar hierarquia consistente entre título, menus, cabeçalhos, rótulos e valores. Aproximar a densidade das pranchas sem reduzir áreas de toque e legibilidade. Não comparar 1080 px do telefone com 590 px da referência sem normalizar largura útil e densidade.
8. **Diálogos:** captura antiga de Nova pasta mostra espaço vazio excessivo. Código atual já reduziu altura, mas ainda calcula altura fixa conforme tipo/comprimento da mensagem. Medir pelo conteúdo, limitar pela área visível e rolar quando necessário; teclado não pode encobrir as ações.

## Conferência por prancha
“Código” abaixo significa análise estática, não tela executada neste Work.

| Prancha | Diferença observada ou identificada no código | Correção e critério de aceite |
| --- | --- | --- |
| 01 — Navegação | Lista sem colunas e barra de cabeçalho; ícones simplificados; a captura denominada grade não prova grade. | Lista com Nome/Tipo/Tamanho, separadores, ordenação identificável e seleção pontilhada. Grade com ícones centralizados e legenda abaixo. Mesmo diretório e arquivos de teste nas duas capturas. Endereço, contagem e ações refletem conteúdo real. |
| 02 — Busca | toggleSearch revela somente EditText. Categoria fica em diálogo separado. Não existe composição Localizar arquivos da prancha. | Painel integrado com título, consulta, local e filtros efetivamente suportados; resultados com pasta/tamanho; estado sem resultados e limpar filtros. Busca ampla/data requer implementação real antes de habilitar controles; não apresentar pasta atual como busca no aparelho. |
| 03 — Seleção/destino | Checkbox é caractere no nome; ações ficam em diálogo. Destino é lista de textos com Usar esta pasta. | Indicador desenhado com estado acessível, destaque azul, contagem/tamanho reais e barra Copiar/Mover/Excluir. Seletor de destino com origem, pasta navegável, destino e confirmar. Mover pasta segue limitação real do motor até ser implementado/testado. |
| 04 — Criar/renomear | Diálogos próprios já existem; captura antiga excessivamente alta. Extensão separada e validação inline constam no código. | Janela compacta com bordas detalhadas, rótulo, campo rebaixado, extensão, cancelar/criar. Manter contexto da pasta; nome inválido não fecha diálogo. Validar com teclado e texto longo em build atual. |
| 05 — Transferências | transferStatus mostra mensagem em diálogo; progresso disponível é por item. Não há painel de fila nem medição de bytes. | Tela própria com operação, origem/destino, progresso real, cancelar, resultados e pendências por item. Porcentagem só com bytes medidos. Sem total conhecido, usar progresso indeterminado. “Movido” somente após remoção confirmada; cópia com original preservado deve ser explicitamente distinguida. |
| 06 — Conflito/exclusão | Conflitos sempre geram outro nome. Não existe seletor Manter ambos/Pular/Substituir. Exclusão usa mensagem textual. | Diálogo de conflito com metadados de origem/destino; habilitar apenas estratégias implementadas. Exclusão com ícone, lista, local, aviso e Cancelar como opção segura. Nada de substituir silenciosamente ou prometer lixeira inexistente. |
| 07 — Prévia/propriedades | Abrir delega a outro app; propriedades é mensagem concatenada. | Propriedades próprias em aba Geral com ícone, tipo, tamanho, caminho, modificação e acesso reais. Prévia interna de imagem precisa carregamento fora da UI e resolução limitada, erro e navegação; para outros formatos, Abrir com. |
| 08 — Acesso/desconexão | Home contém duas opções, mas não há composição de ícones e painel das pranchas. Erro de acesso é genérico; desconexão não tem resumo próprio. | Separar primeiro acesso de home autorizada. Usar seleção/permissões nativas Android. Desconexão mostra operação e itens efetivamente concluídos/pendentes, sem afirmar preservação de itens removidos antes da falha; orientar reconectar e conferir. |
| 09 — Preferências/armazenamento | Tema/ocultos/categoria espalhados pelo menu Exibir. Não há página Preferências nem análise por categoria. | Grupos Aparência/Navegação/Privacidade, controles persistentes e retorno à pasta. Armazenamento apresenta capacidade e análise calculadas dos locais acessíveis, com leitura assíncrona, cancelar e limites de cobertura. Não usar os números ilustrativos das pranchas. |
| 10 — Menus/ações | Captura confirma menu de botões grandes sem ícones ou grupos; Lista/Ícones é uma única opção alternável; menu de arquivo contextual não aparece como desenhado. | Menu plano detalhado, escolhas separadas Lista/Ícones, check consistente, separadores e ícones. Ações contextuais coerentes com arquivo, seleção e provedor; desabilitar o indisponível, sem ação fictícia. |
| 11 — Pasta vazia/acesso removido | Estados são TextViews simples no canto, com botão apenas no erro. | Estado centralizado com pasta/chave e título, explicação curta e ação real. Pasta vazia pode criar se autorizada; revogação pede seleção novamente sem perder favoritos. Manter mensagem distinta de “nenhum resultado”. |
| 12 — Sem espaço/substituição | Não existem estados especializados como os mockups. A substituição não existe no motor. | Mostrar resultado real e permitir outro destino, sem excluir automaticamente cópias parciais. Substituição exige estratégia segura por provedor, verificação e recuperação antes da confirmação visual; até lá, não disponibilizar botão funcional que prometa isso. |
| 13 — Escuro/teclado | Captura escura anterior tinha setas pouco contrastadas; correção está no código, mas falta captura final. Home autorizada não possui grade de categorias. | Seguir exclusivamente 13-tema-escuro-teclado-v2.png. Home com categorias reais, armazenamento e favoritos; validar contraste em todos os estados. Nova pasta dentro de Interno > Trabalho, erro junto ao campo e ações visíveis com teclado. Não confundir onboarding com pasta autorizada. |
| 14 — Favoritos/ajuda | Favoritos abre Locais e favoritos; só pasta atual pode ser favoritada. Ajuda é bloco único de texto em diálogo. | Página de favoritos com caminhos, seleção, Abrir e Remover sem excluir arquivo. Definir/implementar favoritos de arquivos antes de exibi-los. Ajuda com tópicos e navegação, informações/licenças reais e versão do APK. |

## Ordem de implementação
1. Aplicar componentes comuns: relevo, ícones, título/menu/ferramentas/endereço, status e botões. Corrigir a escala antes de multiplicar telas.
2. Navegação/lista/grade, seleção, menus, campos e diálogos. Conferir essas telas contra as pranchas com fixtures.
3. Home autorizada/primeiro acesso, vazios/erros, favoritos, ajuda, preferências e propriedades.
4. Busca, prévias, armazenamento e transferência: implementar também os dados/comportamentos que faltam. UI não deve antecipar resultados inexistentes.
5. Validar cada estado, compilar novamente, executar testes e atualizar o Redmi preservando dados. Repetir captura após qualquer ajuste final.

## Método de conferência no Work com Mac/USB
- Continuar a branch feat/retro-ui-v02 e a PR #2, inspecionando antes alterações locais não enviadas; não substituir pelo código antigo da main.
- Confirmar aparelho e package com.studiocliquim.arquivos/versionCode. A instalação 0.2 prévia é evidência histórica, não comprova estado atual.
- Criar somente uma pasta própria de teste com documentos descartáveis. Não publicar nomes de pastas pessoais em capturas do repositório público.
- Reproduzir cada estado da tabela com dados equivalentes: pasta com seis arquivos, lista/grade, seleção, busca vazia, favorito, arquivo longo, nomes inválidos, falhas seguras de teste.
- Capturar PNG real e vincular à prancha por ID, registrando aparelho, resolução, densidade, fonte, tema, versão e commit.
- Comparar lado a lado com largura útil normalizada; usar sobreposição somente após alinhar viewport, não distorcer a imagem para forçar semelhança.
- Registrar separadamente: semelhança visual, comportamento e acessibilidade. Aprovar um não aprova automaticamente os outros.
- Conferir teclado aberto, fonte ampliada, rotação, largura menor, caminhos longos, TalkBack e contraste. Barra de sistema, teclado, seletor e permissões são controlados pelo Android.
- Testar operações destrutivas só nas fixtures; ensaiar falta de espaço/desconexão em ambiente controlado. Não encher o armazenamento pessoal.
- Não chamar a entrega de “igual às pranchas” com telas faltantes, capturas antigas ou testes interrompidos.

## Evidência mínima de conclusão
- [ ] 14 pares de estados conferidos (incluindo diferenças justificadas por controles nativos/responsividade).
- [ ] Todas as ações visíveis ligadas a operações reais; limitações restantes enumeradas.
- [ ] Capturas novas corretamente nomeadas e sem dados pessoais.
- [ ] Compilação, lint e testes pertinentes executados no commit final.
- [ ] APK/versionCode/checksum identificados; atualização no Redmi confirmada.
- [ ] Inspeção limpa no dispositivo, sem outro app interrompendo testes.
- [ ] README e documentação de versão atualizados; sem afirmar aprovação completa onde houve apenas teste parcial.

Bloqueio atual deste Work: ausência de execução/ADB. A auditoria pode orientar imediatamente o Work conectado ao Mac, mas não substitui a inspeção e as correções no Android solicitadas pelo Müller.
