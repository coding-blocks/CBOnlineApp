# CBOnlineApp
😎⚡App Online para Coding Blocks Online

[![codebeat badge](https://codebeat.co/badges/29c4e81e-f936-47a5-8d9f-2ac15cd9b13d)](https://codebeat.co/projects/github-com-coding-blocks-cbonlineapp-development)
[![Maintainability](https://api.codeclimate.com/v1/badges/fb21e9bcd76c6905d68f/maintainability)](https://codeclimate.com/github/coding-blocks/CBOnlineApp/maintainability)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3871ba02cd654b9585f1d9c8bc0f4365)](https://www.codacy.com/app/championswimmer/CBOnlineApp?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=coding-blocks/CBOnlineApp&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/coding-blocks/CBOnlineApp.svg?branch=development)](https://travis-ci.org/coding-blocks/CBOnlineApp)
[![CircleCI](https://circleci.com/gh/coding-blocks/CBOnlineApp.svg?style=svg)](https://circleci.com/gh/coding-blocks/CBOnlineApp)

Um aplicativo para consumir cursos on-line disponíveis em https://online.codingblocks.com

A aplicação está disponível aqui:

<a href='https://play.google.com/store/apps/details?id=com.codingblocks.cbonlineapp'><img alt='Baixe no Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height="80"/></a>

## Screenshots
<table>
        <tr>
          <td><img src = "app/screenshots/1.png" height = "480" width="270"></td>
          <td><img src = "app/screenshots/2.png" height = "480" width="270"></td>
          <td><img src = "app/screenshots/3.png" height = "480" width="270"></td>
        </tr>
        <tr>
        <td><img src = "app/screenshots/4.png" height = "480" width="270"></td>
        <td><img src = "app/screenshots/5.png" height = "480" width="270"></td>
        <td><img src = "app/screenshots/6.png" height = "480" width="270"></td>
        </tr>
        <tr>
        <td><img src = "app/screenshots/7.png" height = "480" width="270"></td>
        <td><img src = "app/screenshots/8.png" height = "480" width="270"></td>
        </tr>
</table>    

### Bibliotecas usadas e sua documentação

- Retrofit [Docs](http://square.github.io/retrofit/2.x/retrofit/)
- Picasso [Docs](http://square.github.io/picasso/)
- JSON API Converter [Docs](https://github.com/jasminb/jsonapi-converter)
- VideoCipher [Docs](https://legacysite.vdocipher.com/files/android_javadoc/1.0.0-beta1/)
- AndROuter [Docs](https://github.com/campusappcn/AndRouter)
- Koin [Docs](https://github.com/InsertKoinIO/koin)


## Práticas recomendadas de contribuições

### Para contribuintes de primeira vez

Os contribuidores iniciantes podem ler [CONTRIBUTING.md](/CONTRIBUTING.md) arquivo para obter ajuda sobre como criar issues e enviar pull requests.

### Política de branches

Nós temos os seguintes branches

 * **development** Todo desenvolvimento vai para essa branch. Se vocês está contribuindo, você deve fazer um pull request para _development_. PRs para development devem passar em uma checagem de build e em uma checagem de teste unitário no Circle CI.
 * **master** Esta branch contém código terminado. Após suficientes features e correções de bugs são acumuladas em development, nós fazemos um update de versão e fazemos uma release. 
### Práticas de código

Ajude-nos a seguir as práticas recomendadas para facilitar o revisor e o colaborador. Queremos focar mais na qualidade do código do que no gerenciamento da ética de pull requests.

 * Um único commit por pull request
 * Para escrever mensagens de commit, por favor leia o COMMITSTYLE com cuidado. Compreensivamente adira aos guidelines.
 * Siga práticas de design uniformes. A linguagem de design deve ser consistente em todo o aplicativo.
 * O pull request não vai ser mergeado até que os commits sejam squashed. No caso de haverem múltiplos commits no PR, o autor do commit deve fazer o squash, e não os maintainers fazendo cherrypicking ou merging squashes.
 * Se o PR é relacionado a mudanças de front end, por favor anexe screenshots relevantes na descrição do pull request.

### Participe do desenvolvimento

* Antes de ingressar no desenvolvimento, configure o projeto em sua máquina local, execute-o e navegue pelo aplicativo completamente. Pressione qualquer botão que você possa encontrar e veja para onde ele leva. Explore. (Não se preocupe... Nada acontecerá com o aplicativo ou você devido à exploração :wink: A única coisa que acontecerá é que você estará mais familiarizado com o que está aqui e poderá até ter algumas idéias interessantes sobre como melhorar vários aspectos do aplicativo.) 
* Se você gostaria de trabalhar em um problema, insira um comentário na issue. Se já estiver atribuída a alguém, mas não houver sinal de que algum trabalho esteja sendo realizado, deixe um comentário para que o problema possa ser atribuído a você, se o responsável anterior o abandonou completamente.

## Para testadores: Testando o aplicativo
Se você é um testador e deseja testar o aplicativo, há duas maneiras de fazer isso:
1. **Instalando o APK no seu dispositivo:** Você pode conseguir o APK de debug, como também o APK de Release na branch de apk do repositório. A cada merge de PR, ambos APKs são atualizados automaticamente. Então, só baixe o APK e instale no seu device. Os APKs sempre serão os mais recentes.
