# ReContent

Library for parsing sites using a strict system of rules-selectors.

## Connection

1. Add jitpack repository in your root build.gradle at the end of repositories:

  ```gradle
  allprojects {
      repositories {
          // ...
          maven { url 'https://jitpack.io' }
      }
  }
  ```

2. Add the dependency:

  ```gradle
  dependencies {
      implementation 'com.github.0gdump:android-recontent:0.2'
  }
  ```

## Usage

1. To start, initialize Recontent:

    ```kotlin
    val reсontent = ReContent().init(this)
    ```

2. Create a rule structure. The site is parsed with *blocks* described by the RootParser structure, inside which there is a list of rules with selector and callback

    ```kotlin
    reсontent.setupRules({
        rules.add(
            RootParser(
                rootSelector = "div#entrytext.j-e-text",
                textNodeRule = TextNodeRule(callback = { tn -> buildText(tn) }),
                rules = listOf(
                    Rule(
                        selector = "span.j-imagewrapper",
                        callback = { e: Element, t: String? -> buildImage(e, t) }
                    )
                )
            )
        )
    })
    ```

3. Load url

    ```kotlin
    recontent.load(url)
    ```

4. Watch the magic!

## Demo

To view a demo application that demonstrates the library's capabilities, open the project, the *demo module*

## Notes

JSoup and WebView are used under the hood. Loading and parsing performance is not guaranteed and has not been measured. The library was created for research purposes

## License

The library is distributed under [Apache License 2.0](https://github.com/0gdump/android-recontent/blob/master/LICENSE)
