Proposta para aprovação de Müller, pesquisada em 12/11/2026. Aplicativo Android do Estúdio Cliquim para organizar arquivos pessoais com personalidade visual inspirada no Windows 98. Esta etapa define comportamento e prioridades; não autoriza layouts, código, instalação ou publicação.

# Direção recomendada

Um gerenciador de arquivos local, útil sem conta ou conexão, com interface retrô adaptada ao toque. Nome provisório: Cliquim Arquivos. A aparência deve remeter à época sem fingir que o Android virou Windows ou que o app controla pastas privadas do sistema. O diferencial proposto é organização simples, operações confiáveis e identidade visual consistente com a coleção Cliquim.

# Funções propostas para a primeira versão

| Função | Comportamento proposto |
| --- | --- |
| Explorar arquivos | Navegar pelo armazenamento compartilhado autorizado, cartão SD e pendrive quando reconhecidos pelo Android. Mostrar somente locais acessíveis, com indicação de leitura ou escrita. |
| Lista e ícones | Alternar lista detalhada e grade. Ordenar por nome, data, tamanho e tipo; pastas primeiro como preferência. Preservar posição de rolagem ao voltar. |
| Encontrar | Buscar por nome e filtrar tipo, data e tamanho. Começar na pasta atual; oferecer ampliação aos locais autorizados. Informar escopo, andamento e locais não pesquisados. Sem busca dentro dos documentos nesta versão. |
| Selecionar | Toque longo ou comando Selecionar inicia seleção; novos toques marcam itens. Mostrar quantidade e tamanho conhecido. Selecionar tudo deve indicar se vale para o resultado filtrado ou pasta atual. |
| Organizar | Criar pasta, renomear, copiar e mover um ou vários itens. Destino visível antes de confirmar; conflitos nunca resolvidos silenciosamente. |
| Abrir e compartilhar | Prévia simples de imagens e informações do arquivo; outros tipos abrem no aplicativo escolhido pelo usuário. Compartilhar pelo seletor do Android com acesso temporário. |
| Favoritos e recentes | Favoritar pastas sem duplicar arquivos. Recentes significa arquivos usados no Cliquim, a menos que outra fonte suportada seja claramente indicada. |
| Propriedades | Nome, tipo, tamanho disponível, localização, datas fornecidas e capacidades. Não inventar data de criação, dono ou tamanho quando a fonte não informar. |
| Transferências | Fila com origem, destino, progresso e resultado por item. Cancelamento seguro; pausa e retomada somente quando comprovadas para aquela origem. |
| Exclusão | Primeira versão: confirmação explícita de exclusão permanente, com nome, quantidade e local. Lixeira fica para uma evolução com restauração validada por origem. |
| Armazenamento | Mostrar espaço do volume quando disponível e tamanho dos locais autorizados. Não apresentar leitura parcial como análise de todo o aparelho. |
| Preferências | Tema clássico claro e escuro, seguindo o sistema como opção; tamanho do texto e dos ícones, contraste, ordenação, arquivos ocultos opcionais e sons desligados por padrão. |
| Categorias e atalhos | Downloads quando acessível, Imagens, Vídeos, Áudios, Documentos e arquivos grandes nos locais autorizados. São filtros, não movimentações físicas dos arquivos. Favoritos fixáveis. |
| Concluir e continuar | Ao terminar uma cópia, oferecer Abrir destino e Voltar à origem. Manter a pasta de destino recente sem selecionar silenciosamente um local desconectado. |
| Atualizar pasta | Comando Atualizar acessível também por botão/menu; atualizar após operações sem perder rolagem nem seleção válida. |

# Acesso aos arquivos e permissões

Há dois caminhos. O Storage Access Framework permite ao usuário conceder acesso a arquivos ou pastas específicas, sem uma permissão ampla de armazenamento. Algumas raízes e pastas são restritas nas versões recentes do Android, incluindo Android/data e Android/obb. Permissões persistentes podem ser revogadas ou perder utilidade se o arquivo mudar; o app deve permitir reconectar o local. [Documentação SAF](https://developer.android.com/training/data-storage/shared/documents-files).

Para a primeira versão, recomendo acesso amplo ao armazenamento compartilhado como modo principal, acompanhado de um modo funcional por pastas escolhidas via SAF. A função central é explorar e organizar arquivos entre locais, tornando o acesso amplo coerente com o produto; não é permissão para funções secundárias. Sua inclusão na versão distribuída depende de justificar por que SAF/MediaStore não atendem ao escopo, preencher a declaração e obter a revisão da Play. Ser gerenciador elegível não garante aprovação. Se houver recusa, remover a permissão da versão enviada e anunciar somente o escopo realmente acessível pelo SAF. [Política da Google Play](https://support.google.com/googleplay/android-developer/answer/10467955).

Mesmo com acesso amplo, o aplicativo não ganha root nem acesso aos dados privados de outros apps. O suporte a SD/USB depende do aparelho, volume e permissões. Não prometer recuperar arquivos apagados, acessar conversas privadas do WhatsApp, limpar caches internos de outros apps ou administrar o sistema. [Limites técnicos](https://developer.android.com/training/data-storage/manage-all-files). Provedores de nuvem disponíveis pelo SAF podem ser usados apenas dentro das capacidades concedidas; isso não significa integrar ou sincronizar todo o Drive. [Provedores de documentos](https://developer.android.com/guide/topics/providers/document-provider).

# Regras para não perder arquivos

Estas são decisões propostas de produto e critérios futuros de teste:

- Recortar apenas prepara uma movimentação; o original permanece até o destino estar concluído e validado.

- Entre volumes, copiar para um destino novo, fechar a gravação, reler e comparar conteúdo antes de apagar a origem. Tamanho sozinho não prova integridade. Se a verificação não puder concluir ou a origem mudar, manter o original e informar o motivo; se apenas a exclusão falhar, informar Copiado, original mantido. Otimizações futuras precisam preservar esta garantia.

- Não prometer atomicidade entre provedores. Registrar o que concluiu, falhou ou ficou incerto e conferir antes de repetir.

- Em conflito, oferecer Manter ambos e Pular; Substituir exige escolha explícita. Mesclar pastas precisa tratar conflitos internos individualmente. O primeiro lançamento não precisa permitir substituir pastas inteiras.

- Em pouco espaço, permissão revogada, cartão removido ou processo encerrado, preservar a origem e identificar temporários criados pelo app. Nunca apagar arquivos preexistentes como limpeza.

- Impedir mover uma pasta para dentro dela mesma; detectar origem igual ao destino e arquivos alterados durante a transferência.

- Não prometer Desfazer universal. Se não houver recuperação real, mostrar claramente que a exclusão é permanente.

- Restauração encontra conflito? Restaurar com outro nome ou pedir decisão; nunca sobrescrever automaticamente.

- Lixeira própria fica fora da primeira versão. Uma evolução deve definir armazenamento por volume, retenção, espaço ocupado, restauração, conflitos e consequências da desinstalação. Não confundir retirar da listagem com recuperar o conteúdo, nem prometer recuperação após exclusão permanente.

# Experiência e linguagem

Proposta de navegação conceitual, ainda sem desenhar telas: locais e favoritos levam à pasta; pasta leva ao arquivo; ações de organização permanecem disponíveis na seleção. Celular usa uma área principal; tablet pode ganhar árvore lateral sem miniaturizar a versão de desktop. Voltar fecha primeiro diálogos e seleção, depois retorna no histórico; Subir vai à pasta superior e tem significado distinto. O app preserva a pasta atual ao retornar de outro aplicativo. [Referência Android para adaptação](https://developer.android.com/develop/ui/compose/layouts/adaptive).

Usar um toque para abrir e toque longo para selecionar. Oferecer Selecionar no menu para não depender de um gesto oculto. Arrastar pode ser complemento no futuro, nunca a única forma de mover. Para o toque, preferir Mover para em vez de exigir entender a sequência Recortar e Colar. Esses comandos clássicos podem continuar disponíveis para teclado ou menu.

Textos sugeridos: Arquivos, Editar, Exibir, Favoritos, Ajuda, Nova pasta, Copiar para, Mover para, Renomear, Propriedades, Manter ambos e Excluir permanentemente. São propostas em português para Android, não transcrição verificada dos menus brasileiros do Windows 98. Erros precisam explicar o que aconteceu e uma saída: Permissão removida — selecionar pasta novamente; Pendrive desconectado — original preservado; 8 itens copiados, 2 pendentes — revisar.

# Direção visual para aprovação

A [galeria enviada](https://guidebookgallery.org/screenshots/win98se/) cataloga Explorer, pesquisa, lixeira, diálogos e propriedades do Windows 98 SE. Os links relativos de algumas imagens retornaram erro; foram localizados os endereços diretos do [gerenciador](https://guidebookgallery.org/pics/gui/system/managers/filemanager/win98se.png) e da [lixeira](https://guidebookgallery.org/pics/gui/system/features/trashcan/win98se.png). A inspeção visual em tamanho integral ainda deve ser concluída antes do desenho das telas; nenhuma prancha foi criada nesta etapa.

Direção proposta: superfícies cinza, barra de título azul, área de arquivos clara, seleção azul e botões com relevo discreto. Ícones em pixel art autorais; texto principal nítido e escalável, não rasterizado. Referências de cor para experimentar após aprovação: superfície #C0C0C0, azul #000080, conteúdo #FFFFFF, texto #111111. Contraste deve ser medido em cada combinação final. Não usar botões minúsculos de fechar/minimizar só para imitar um computador.

Adotar alvos de toque de pelo menos 48 dp, texto escalável e contraste mínimo de 4,5:1 para texto comum, com os ajustes previstos para texto grande. TalkBack anuncia nome, tipo, seleção e estado da operação; seleção não depende só de cor. Respeitar redução de animações e fornecer alternativa a qualquer gesto de arraste. [Acessibilidade Android](https://developer.android.com/guide/topics/ui/accessibility/apps).

# O que fica para depois

Primeira evolução sugerida após validar o núcleo: renomeação em lote com prévia, ZIP básico com criação/extração segura e alternância entre duas pastas (abas no celular, painel duplo em tela ampla). Depois: mapa de espaço, duplicados com confirmação por conteúdo, transferência com computador, SMB/SFTP, nuvem própria, lixeira por origem e cofre criptografado. Não incluir no lançamento limpeza automática, root, instalação de APKs ou IA analisando documentos. ZIP exige proteção contra caminhos fora do destino, links e expansão excessiva. [Risco Zip Path Traversal](https://developer.android.com/privacy-and-security/risks/zip-path-traversal).

# Segurança e preparação para venda

Compartilhar arquivos por content URI e concessão temporária, sem expor diretórios inteiros ou caminhos privados. [Compartilhamento seguro Android](https://developer.android.com/training/secure-file-sharing). Recomendo primeira versão sem anúncios e sem envio do conteúdo dos arquivos para servidores. Diagnósticos não devem registrar nomes, caminhos ou miniaturas pessoais. Política de privacidade e Data safety precisam refletir o comportamento do app e de todos os SDKs, mesmo se o processamento for local. [Política de dados](https://support.google.com/googleplay/android-developer/answer/10144311).

A Google Play usa Android App Bundle para distribuição; APK continua útil para testes locais. Será necessário definir assinatura de produção, cópia segura da chave de upload, canal de testes e ficha com recursos realmente implementados. [Publicação](https://support.google.com/googleplay/android-developer/answer/9859152). A documentação consultada exige API alvo 36 para novos apps a partir de 31/08/2026; reconferir na submissão. [API alvo](https://support.google.com/googleplay/android-developer/answer/11926878). Para contas pessoais criadas após 13/11/2023, há teste fechado com pelo menos 12 participantes por 14 dias contínuos antes de solicitar produção. [Testes da Play](https://support.google.com/googleplay/android-developer/answer/14151465).

Inspiração histórica não é licença para redistribuir ícones, fontes, sons, logos ou imagens extraídos do Windows. Usar marca Cliquim e recursos autorais/licenciados, mantendo inventário de origem e permissões. Os ícones já existentes no projeto precisam dessa verificação antes da venda. Não apresentar o produto como oficial da Microsoft. [Direitos de conteúdo Microsoft](https://www.microsoft.com/en-us/legal/intellectualproperty/copyright/permissions) e [marcas](https://www.microsoft.com/en-us/legal/intellectualproperty/trademarks). A revisão de direitos e da identidade comercial permanece uma etapa antes da publicação.

# Alternativas questionadas e refinadas

| Ideia inicial | Problema | Proposta refinada |
| --- | --- | --- |
| Explorer idêntico no celular | Menus e duplo clique dificultam o toque | Aparência histórica, interação móvel |
| Tudo acessível após uma permissão | Android mantém áreas protegidas | Explicar acesso real por local |
| Lixeira para qualquer arquivo | Nem toda fonte permite restaurar | Lixeira só com garantia técnica, exclusão explícita nos demais |
| Mover equivale a copiar e apagar | Falhas entre volumes podem perder dados | Confirmar destino e preservar origem em falhas |
| Busca em todo o celular | Cobertura varia por autorização e provedor | Exibir escopo e resultados parciais |
| Anúncios para financiar desde o começo | SDKs e interrupções prejudicam um app de arquivos | Avaliar venda direta sem anúncios inicialmente |
| Biblioteca de todos os recursos avançados | Amplia risco antes de validar o básico | Entregar organização local confiável primeiro |

# Testes exigidos na fase de implementação

1. Selecionar, copiar e mover um arquivo e lotes, dentro e entre volumes, comprovando origem e destino.

2. Interromper transferência por falta de espaço, remoção de SD/USB, revogação de permissão e encerramento do processo; verificar ausência de perda e duplicação silenciosa.

3. Conflitos de nomes, arquivos vazios/grandes, extensões desconhecidas, nomes longos e acentos.

4. Resultado de busca parcial, provedor offline e conteúdo alterado durante a operação.

5. Exclusão/restauração por tipo de origem e conflito na restauração; sem promessas de recuperação não testadas.

6. Pastas grandes com carregamento progressivo e operações fora da thread de interface; medir em aparelho simples antes de definir metas de desempenho.

7. TalkBack, fonte ampliada, toque, teclado, rotação e tela dividida, incluindo contraste real.

8. Emuladores em versões Android suportadas e pelo menos um aparelho físico; comparar Pixel/Android padrão, Samsung e Xiaomi quando houver aparelhos disponíveis.

9. Escopo exato de permissões, compartilhamento temporário e ausência de conteúdo pessoal nos logs.

# Pesquisa na Google Play — 12/11/2026

Amostra de gerenciadores consolidados e bem avaliados, escolhidos por nota, volume de avaliações e funções relevantes. Não é um ranking exaustivo da loja. Usamos notas do cabeçalho das fichas consultadas; região, aparelho, cache e atualização podem alterar os números. A seção de telefone do Cx, por exemplo, mostrava 4,8 enquanto o cabeçalho mostrava 4,7. Downloads são faixas acumuladas, não usuários ativos ou vendas. Foram lidas fichas e resenhas públicas visíveis, sem instalar ou testar os concorrentes.

| Aplicativo e fonte | Nota do cabeçalho | Avaliações / downloads | Ideia aproveitável |
| --- | --- | --- | --- |
| [Cx Explorador](https://play.google.com/store/apps/details?id=com.cxinventor.file.explorer&hl=pt_BR) | 4,7 | 354 mil / 10 milhões+ | Organização simples, análise de espaço e atualização dos diretórios. |
| [File Manager Plus](https://play.google.com/store/apps/details?id=com.alphainventor.filemanager&hl=pt_BR) | 4,7 | 1,72 milhão / 100 milhões+ | Categorias, Downloads e retorno à origem após copiar. |
| [Files do Google](https://play.google.com/store/apps/details?id=com.google.android.apps.nbu.files&hl=pt_BR) | 4,5 | 9,12 milhões / 5 bilhões+ | Busca simples, revisão do que ocupa espaço e compartilhamento. |
| [Solid Explorer](https://play.google.com/store/apps/details?id=pl.solidexplorer2&hl=pt_BR) | 4,5 | 159 mil / 10 milhões+ | Duas pastas abertas, renomeação em lote, operações retomáveis e personalização. |
| [X-plore](https://play.google.com/store/apps/details?id=com.lonelycatgames.Xplore&hl=pt_BR) | 4,4 | 257 mil / 10 milhões+ | Árvore de pastas, mapa de armazenamento e navegação por compactados. |

# O que as avaliações sugerem

As resenhas abaixo são relatos individuais, não comprovação técnica nem medição da frequência dos problemas. A lição para o Cliquim é uma interpretação nossa.

| Evidência pública | Decisão proposta |
| --- | --- |
| No Cx, avaliações de agosto e outubro de 2026 elogiam ausência de anúncios, conexões e atualização de pastas. [Fonte](https://play.google.com/store/apps/details?id=com.cxinventor.file.explorer&hl=pt_BR) | Manter o fluxo de organização sem interrupções e incluir Atualizar desde o início. |
| No File Manager Plus, avaliações de agosto e setembro de 2026 pedem personalização e tema escuro; o histórico registra retorno à origem após colar. [Fonte](https://play.google.com/store/apps/details?id=com.alphainventor.filemanager&hl=pt_BR) | Tema escuro passa ao núcleo; Abrir destino e Voltar à origem entram no fim da operação. |
| No Solid, uma avaliação de outubro de 2026 destaca renomeação em lote; outras de julho e setembro relatam reconexão e extração problemática. [Fonte](https://play.google.com/store/apps/details?id=pl.solidexplorer2&hl=pt_BR) | Priorizar renomeação em lote na primeira evolução e testar regressões de conexão/ZIP antes de ampliar funções. |
| No X-plore, resenhas de julho/agosto de 2026 relatam travamentos e incômodo com anúncios. [Fonte](https://play.google.com/store/apps/details?id=com.lonelycatgames.Xplore&hl=pt_BR) | Orçamento de memória para miniaturas e fila que não paralisa navegação; recomendar produto pago sem anúncios. |
| No Files do Google, resenhas de agosto de 2026 relatam falha em ações de liberar espaço. [Fonte](https://play.google.com/store/apps/details?id=com.google.android.apps.nbu.files&hl=pt_BR) | Mostrar resultados e falhas por item; evitar botão genérico de limpeza sem explicar o que será excluído. |

# Novas funções candidatas e experiência

| Função | Como funcionaria no Cliquim | Prioridade |
| --- | --- | --- |
| Categorias rápidas | Mostrar arquivos por tipo sem movê-los; sempre indicar os locais cobertos. Recentes usados e arquivos modificados recentemente são filtros distintos. | Núcleo |
| Tema escuro retrô | Preservar relevo e linguagem visual com cores adequadas à leitura noturna. Seguir sistema ou escolha manual. | Núcleo |
| Pós-transferência útil | Abrir destino, voltar à origem e consultar itens pendentes; uma falha não bloqueia toda a navegação. | Núcleo |
| Renomeação em lote | Adicionar prefixo, substituir trecho e numerar a partir de um valor. Prévia nome antigo → novo, preservando extensão por padrão. Conflitos precisam ser resolvidos antes; relatório de execução parcial, sem promessa de desfazer universal. | Primeira evolução |
| Duas pastas abertas | Alternar origem e destino no celular; lado a lado quando couber. Copiar/mover por comando; arraste opcional e acessível. | Primeira evolução |
| ZIP básico | Compactar seleção e extrair em pasta escolhida. Exibir destino, conflitos e limites. Não prometer todos os formatos/encriptações de início. | Primeira evolução |
| Mapa de espaço | Lista e gráfico dos maiores itens autorizados, com detalhes antes da exclusão. Não chamar arquivos pessoais grandes de lixo. | Evolução |
| Pastas personalizadas | Cor ou símbolo escolhido pelo usuário salvo no app, sem renomear nem modificar a pasta real. Não depender só da cor. | Evolução |
| Receber arquivos para organizar | Aparecer como destino de Compartilhar; usuário escolhe pasta e confirma cópia. Nenhuma remoção automática da origem. | Evolução, após validar formatos e permissões |
| Envio ao computador | Sessão local temporária, autenticação e botão Encerrar; avaliar solução segura antes de criar servidor. Compartilhar pelo Android já cobre o básico. | Evolução |
| Duplicados | Mesmo nome não basta: comparar conteúdo antes de classificar. Usuário escolhe o que manter. | Evolução |
| Lixeira por local | Oferecer apenas onde retenção/restauração forem reais; explicar onde ficam os dados e como liberar o espaço. | Dependente de validação |

# SAF e acesso amplo — decisão operacional

| Critério | Pastas escolhidas pelo SAF | Acesso amplo ao armazenamento compartilhado |
| --- | --- | --- |
| Entrada | Usuário escolhe arquivos/pastas no seletor Android. | Usuário concede acesso especial nas configurações. |
| Alcance | Apenas concessões efetivas, respeitando raízes restritas. | Mais abrangente no armazenamento compartilhado; dados privados de apps continuam protegidos. |
| Capacidades | Variam entre fonte local, SD, USB e provedor remoto; nem todo item permite escrever, mover ou excluir. | Acesso não transforma todos os volumes em graváveis nem elimina limites do sistema. |
| Busca | Cobertura limitada às concessões e capacidades do provedor. | Pode indexar conteúdo acessível mais amplamente; comunicar o que ficou fora. |
| Play | Adequado quando seleção de pastas resolve a proposta. | Função central de gerenciador é elegível, mas exige justificativa e declaração/revisão. |
| Recusa/revogação | Reconectar pasta ou trabalhar com as demais. | Oferecer modo por pastas e explicar a cobertura reduzida. |

Fontes: [SAF](https://developer.android.com/training/data-storage/shared/documents-files), [acesso amplo](https://developer.android.com/training/data-storage/manage-all-files), [política da Play](https://support.google.com/googleplay/android-developer/answer/10467955). Decisão: sustentar a necessidade real da permissão com funções demonstráveis; não usar a existência de concorrentes como garantia de aprovação. Se ela não for aceita, remover essa permissão da versão submetida e ajustar a oferta ao modo limitado.

# Comportamentos obrigatórios nas operações

As regras seguintes são especificação proposta, ainda não implementada. Capacidades de leitura, criação, escrita, renomeação, cópia, movimento e exclusão devem ser verificadas por origem e item. As flags do Android distinguem operações suportadas, arquivos parciais e metadados desconhecidos. [DocumentsContract.Document](https://developer.android.com/reference/android/provider/DocumentsContract.Document). Renomear pode devolver outra URI; atualizar referências. Mover e remover de um pai são operações diferentes de excluir o documento. [DocumentsContract](https://developer.android.com/reference/android/provider/DocumentsContract).

| Situação | Resposta esperada |
| --- | --- |
| Cópia interrompida | Registrar concluídos e incompletos. Preservar origem. Limpar somente temporários próprios identificados; se a limpeza falhar, informar. |
| App encerrado ou reiniciado | Reconciliar registro com origem e destino antes de repetir. Estado incerto não vira sucesso nem nova cópia automática. Retomada por byte só após comprovar suporte e identidade do conteúdo. |
| Mover entre volumes | Copiar, fechar, reler e comparar conteúdo; só excluir original estável após verificação. Falha na verificação mantém origem. Exclusão parcial da pasta é relatada por item. |
| Movimento nativo no mesmo provedor | Usar apenas quando suportado e validado; não prometer cópia intermediária ou restauração. Resultado incerto exige consulta, não repetição cega. |
| Nome já existe | Manter ambos, pular ou substituir explicitamente. Revalidar conflito no momento da gravação. Sem substituir em lote por padrão. |
| Substituição sem transação segura | Preservar destino antigo até novo conteúdo validado quando possível; se a fonte não permite isso, oferecer Manter ambos/Pular em vez de sobregravação arriscada. |
| Pasta sobre pasta | Mesclar item a item com conflitos visíveis; nunca apagar a pasta anterior inteira para substituí-la. |
| Cancelamento | Parar de agendar novos itens; cancelar a operação atual conforme suporte. Mostrar Cancelando até confirmação. O que concluiu permanece registrado. |
| SD/USB removido | Suspender operações afetadas e preservar originais ainda existentes; outras operações independentes podem continuar. Reconhecer o mesmo volume ao reconectar, sem confiar apenas no nome. |
| Origem mudou durante a cópia | Não apagar o original. Marcar para revisão e nova tentativa. Comparações de tamanho/data ajudam a detectar mudanças, mas não garantem snapshot em todo provedor. |
| Pouco espaço ou tamanho desconhecido | Não apresentar previsão como garantia. Validar espaço conhecido, tratar falha durante escrita e manter originais. |
| Provedor remoto indisponível | Diferenciar sem internet, sessão expirada e permissão revogada quando houver evidência. Preservar conteúdo/cache seguro e permitir reconectar. |
| Exclusão e recuperação | No núcleo, confirmação de exclusão permanente. Futuro recurso de lixeira exige teste por origem, conflito na restauração e retenção definida. |

# Busca grande, estados vazios e acessibilidade

Busca deve mostrar o escopo, resultados parciais e uma ação Parar. Enumerar fora da interface, limitar concorrência e memória, carregar miniaturas sob demanda. Usar paginação/pesquisa nativa quando a fonte suportar; não presumir que todos os provedores paginam. Índice local precisa indicar atualização e invalidar resultados ao perder acesso. Nome e metadados ficam locais; nenhuma indexação de conteúdo ou envio para IA no núcleo. Não mostrar zero resultados antes de concluir nem apresentar resultados de pastas desconectadas como atuais.

| Estado | Mensagem/ação proposta |
| --- | --- |
| Nenhum local autorizado | Escolha uma pasta para começar — Selecionar pasta. Explicar também o modo amplo quando aplicável. |
| Pasta vazia | Esta pasta está vazia — Nova pasta, se permitida. |
| Busca sem resultado | Nada encontrado neste local — Limpar filtros ou Escolher outro local. |
| Busca ainda em andamento | Procurando em… — resultados parciais e Parar. |
| Permissão perdida | Acesso removido — Selecionar pasta novamente. |
| Local só leitura | Você pode abrir e copiar; alterações não estão disponíveis aqui. |
| Pendrive ausente | Reconecte o pendrive para continuar — Voltar aos arquivos. |
| Arquivo indisponível | Este item não está disponível agora — Atualizar ou Reconectar, conforme causa conhecida. |
| Lote parcialmente concluído | 8 concluídos, 2 pendentes — Ver detalhes e Tentar pendentes. |
| Formato sem app compatível | Nenhum aplicativo disponível para abrir este formato — Compartilhar ou ver Propriedades. |

Não depender apenas de cor, arrastar, toque longo ou gestos. Oferecer comandos com rótulos, foco previsível, contador de seleção anunciado e atualizações de progresso sem inundar o TalkBack. Ação indisponível precisa ter explicação acessível. Fonte ampliada não pode esconder Confirmar/Cancelar; navegação e restauração da posição devem funcionar com teclado, leitor de tela e toque.

# Critérios para avançar

Primeiro aprovar funções e prioridades. Depois validar tecnicamente acesso, capacidades, transferências interrompidas e busca em pastas grandes. Só então desenhar os fluxos/telas com referências históricas inspecionadas e ativos autorais/licenciados. A consulta atual não instalou concorrentes nem concluiu a inspeção visual integral da galeria. Nenhuma aprovação de implementação foi presumida.

# Autorização e situação de execução

Müller autorizou nesta conversa criar um repositório no GitHub, desenvolver um APK e instalá-lo no celular após ficar pronto. A autorização substitui a pausa anterior para aprovação. O escopo continua sendo a primeira versão descrita aqui; recursos de evolução permanecem posteriores. Nenhum repositório, APK ou instalação foi concluído nesta sessão. A ferramenta de terminal retornou No usable execution environment is available; não há controle de computador/USB disponível e o conector GitHub exposto não oferece criação de repositório. A execução depende de disponibilizar um ambiente com essas capacidades. A inspeção visual integral da referência histórica também permanece pendente.

# Resposta consolidada ao Mateiro

Estratégia recomendada: acesso amplo ao armazenamento compartilhado no modo principal, sujeito à declaração/revisão da Play, e SAF como modo limitado funcional. Nenhum deles concede root ou acesso irrestrito aos dados privados de outros apps; Android/data e Android/obb de outros apps continuam fora do escopo. SD/USB precisam ser reconhecidos pelo aparelho e autorizar as operações necessárias. A revisão da Play acontece antes da distribuição, não é garantida pela existência de concorrentes.

Movimentação entre volumes: registrar operação e identidades de origem/destino; criar arquivo novo próprio no destino; copiar e fechar; reler e verificar conteúdo; confirmar que a origem não mudou; então concluir o destino e apagar a origem por item. Só tamanho igual não valida conteúdo. Se o provedor não permitir verificação confiável ou não for possível assegurar a estabilidade necessária da origem, preservar o original e finalizar como cópia, explicando a pendência. Não há garantia absoluta contra defeito físico, remoção brusca após gravação ou alterações externas simultâneas. O app não deve prometer backup nem risco zero.

Conflitos: Manter ambos ou Pular como caminhos seguros. Substituir somente por escolha explícita e em origem/destino onde o comportamento possa ser validado sem apagar o arquivo anterior antes de concluir o novo. Caso contrário, desabilitar Substituir naquele local. Mesclar pastas por item; nunca apagar uma pasta inteira para resolver conflito. Revalidar o nome no momento da gravação.

Pendrive desconectado: interromper os itens afetados, manter a origem ainda existente e registrar concluídos/incompletos/incertos. Ao reconectar, verificar identidade do volume e conteúdo antes de tentar novamente. Não repetir automaticamente operações incertas, não apagar temporários desconhecidos nem reiniciar tudo gerando duplicatas. Uma leitura de retorno não garante que todos os controladores físicos já persistiram dados; mostrar a orientação de ejetar com segurança e não afirmar proteção absoluta.

Referências históricas: ainda não houve inspeção visual em tamanho integral. A galeria e URLs diretas foram localizadas, mas as ferramentas disponíveis não exibiram os pixels. Cores e linguagem visual são propostas, não uma reprodução histórica já conferida. Nenhuma tela foi criada.
