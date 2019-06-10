# TinyHttp

---

## What is this

TinyHttp is some kind of toy project for java programming  learning. That is why I choose the MIT LICENSE for it.

## Function list

- [x] Load customed settings from json file.
- [x] Response for basic http GET method. (including brainy files)
- [x] 404 status code support.
- [x] 403 status code support.
- [x] Directory content list.
- [ ] Support for POST method. (Untested now)
- [ ] Add support for Content-Encoding such as gzip, deflate and br.
- [ ] Condition request.
- [ ] Support for dynamic resources requests.

## Dependency

|  name |   version    |
| ----: | :----------: |
| maven | 3.5 or later |
|   jdk |     11+      |

- Then the mvn command will automatically download and import all other dependences the project needs

- For pensonal reason, I have started this project based on jdk-11. Besides, No compatibility tests with jdk-8 has been done.

## Run it

- Compile it

    ```shell
    mvn compile
    ```

- Package into single jar file.

    ```shell
    mvn package
    ```

- Run with sample configure.

    ```shell
    cd $(the root directory of this project)
    java -jar target/tinyhttp-$(version).jar -c resources/test/testWeb.json
    ```

- Test it with your web browser, at port 80.

## Develop it

You can fork it into your github repo, or simply clone it. Then work on your own local git branch.
