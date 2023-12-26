# Changelog

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

## 1.14.5 - 2023-12-26

### Fixed

-

## 1.14.4 - 2023-10-23

### Fixed

- Fixed an issue with TAML file type re-registering over and over
- Fix recursive loop in type lookup of AssetDatabase

## 1.14.3 - 2023-09-13

### Fixed

- Fixed an exception in the object name completion logic
- NPE in Method Call annotations

## 1.14.2 - 2023-09-13

### Fixed

- Updated compatibility range

## 1.14.1 - 2023-03-06

### Fixed

- Stop trying to resolve paths in non-TAML XML files
- Highlighting of detected paths in TAML files

## 1.14.0

### Added

- Autocomplete for ModuleDatabase built-in object

### Changed

- Cleaned up AST a little
- Add parentheses when auto-completing method calls

### Fixed

- Removed logging of noisy warning
- Autocomplete of objects
- Updated dependencies
- ModuleDatabase warning

## 1.13.0

### Added

- .mis and .gui file type association

### Fixed

- Find Objects defined in TAML when auto-completing
- Do not show other function declarations in auto-complete for new function declarations
- Fix auto-complete for functions on variables
- Fix highlight and documentation of built-in methods

## 1.12.4

### Fixed

- Updated Java version to 17

## 1.12.3

### Fixed

- Improved parsing of nested SimObject declarations

## 1.12.2

### Fixed

- Fixed ClassNotFound for ImportAssetAction in legacy builds

## 1.12.1

### Fixed

- Fixed a NoSuchElementException in getRootDir
- Fixed ClassNotFound for ImportAssetAction in legacy builds

## 1.12.0

### Added

- Added "Attach to existing process" debug functionality
- Support for legacy version of IDEA, supporting 2021.1 and above

### Fixed

- Improved auto-detection of script root

## 1.11.6

### Fixed

- Normalize run configuration paths to eliminate back-slashes on Windows-based systems in some IDE's.
- Added a separate release-track for legacy versions of IDEA to extend support for 2021 versions of IDEA
- Fixed an issue with back-slashes in paths in Rider

## 1.11.5

### Changed

- Now using relative paths in run configuration

### Fixed

- Fixed NPE with the message "The parent of ... was null" and changed it to an invisible warning
- A bug in run-configuration made it impossible to change it

## 1.11.4

### Deprecated

- Upgraded to new UI DSL, breaking compatibility with IDEs before version 2021.3

### Fixed

- Use local Sentry instance to avoid error reports from other plugins
- An InvalidPathException
- Added more debug information to a NPE
- Made ShapeAsset Import work for newer versions of Torque3D 4.0

## 1.11.3

### Fixed

- Updated dependencies
- Build for Jetbrains 2022.1

## 1.11.2

### Fixed

- Updated security
- Handle NPE

## 1.11.1

### Fixed

- Object representation in debugger views

## 1.11.0

### Added

- Report errors directly through IDE
- Improved variables view, so objects are expandable

### Fixed

- Fixed viewing of variables with spaces

## 1.10.6

### Added

- Experimental Asset Import functionality

### Changed

- Added a working directory and main script setting to run configuration
- Added a progress bar during rebuild of exports

### Fixed

- Debugger socket wouldn't close on Linux
- Improved Console Dump parsing

## 1.10.5

### Fixed

- A NoSuchElementException in TSFoldingBuilder when a switch is being written and a LBRACE exists but no RBRACE

## 1.10.4

### Fixed

- Rendering of see docstring elements after a codeblock
- NPE for formatting block in empty files

## 1.10.3

### Fixed

- Docstrings now fix illegal line-endings in attributes
- "See" docstring now properly renders

## 1.10.2

### Fixed

- A reference error on TSFunctionCallExpr
- Improved reference resolution of namespaced functions
- ```
  funtion AIPlayer::
  ```

## 1.10.1

### Fixed

- ```
  Foo
  ::
  // Some Comment
  Bar
  ```
- Auto-complete of namespace function wasn't working

## 1.10.0

### Added

- AutoCompletion of paths in TAML
- AutoCompletion of paths in TorqueScript
- Go To File for paths in TAML
- Support for module-references in paths

## 1.9.1

### Added

- Default engine API and schema library files, for smoother "getting-started" experience

## 1.9.0

### Added

- - Currently, this only works with files ending in `.taml` because of a quirk in IDEA

## 1.8.0

### Added

- Auto-completion for fields

### Changed

- Auto-completion will now only show class names when instantiating new objects

## 1.7.1

### Fixed

- Named object function reference resolution
- Trim empty description elements in documentation

## 1.7.0

### Added

- Support for TorqueScript docstrings (comments starting with three slashes)
  on functions

### Fixed

- Some Null Pointer Exceptions
- Rendering of @see attributes in docstrings
- Preview code snippets in code style editor
- Some reference exceptions
- Debugger issues with latest version of Torque3D 4.0
- Made the IDE more robust against engine crashes

## 1.6.7

### Fixed

- Improve error handling when breakpoint file is not found
- Resolve files from root directory upon hitting a breakpoint

## 1.6.6

### Fixed

- This solves an issue on OSX where the game executable is not in the root folder

## 1.6.5

### Fixed

- Fixed a race-condition when connecting to debugger via telnet

## 1.6.4

### Changed

- Removed dependency on `com.intellij.java`

### Fixed

- Improved error handling in Rebuild Exports action

## 1.6.3

### Fixed

- Breakpoints added after application had started would get the
  string "null" as a condition if no condition were specified. Now it correctly defaults to "true"

## 1.6.2

### Fixed

- Global Functions are no longer incorrectly prefixed with :: in auto-completion

## 1.6.1

### Fixed

- Handle more illegal XML chars in the Rebuild Exports action

## 1.6.0

### Added

- Auto-show local variables when breakpoint is hit

### Changed

- Specify compatibility range of plugin to be anything from 211

### Fixed

- Parent references in object initializers are now properly resolved and highlighted

## 1.5.1

### Added

- Hyperlinks for simple file paths in strings

### Changed

- Classified find usages results on objects
- Context-aware completion for method calls on named objects and when type-analysis succeeds
- Context-aware completion for callbacks

### Fixed

- Fixed a bug with formatter indenting body on new instance expressions
- Find usages for local variables is now working

## 1.5.0

### Added

- Experimental formatter support

### Changed

- Improved documentation for quick-navigation

### Fixed

- Fixed an issue with folding in switch statements
- Technical Debt: Renamed brack and paren token types
- Fixed highlighting of `default` keyword

## 1.4.0

### Added

- Run To Position in Debugger
- Conditional breakpoint support
- Support for generating and importing engine exports
- Documentation support for builtin functions and classes
- Completion support for builtin functions and classes

### Fixed

- Namespaced functions had inversed lookup logic

## 1.3.1

### Added

- Go to symbol for Objects and Global Variables

### Fixed

- - Global Variables
- Function Names
- Object Names
- Step-by-step debugging wasn't working properly, should be working better now

## 1.3.0

### Added

- - Line breakpoints
- Step-by-step debugging
- On-hover evaluation of variables
- Evaluation dialog support
- Stack frame with function arguments

## 1.2.1

### Fixed

- Parameters did not pass as variables, fixed with a change to the PSI
- Find usages for functions

## 1.2.0

### Added

- Basic run configuration
- - Local Variables
- Global Variables
- Object names
- Functions
- Methods
- Keywords
- Brace Matchine
- Commenter
- - Global Variables
- Global Functions
- Namespaces Functions
- Object Names
- Basic code folding for blocks
- Navigate to function

## 1.1.0

### Added

- Go to definition on method calls
- Go to definition on object instances
- Go to definition on global variables
- Syntax highlighting for functions and instances

### Fixed

- Improvements to grammars to handle more edge-cases

## 1.0.0

### Added

- Plugin icon

### Changed

- Update version to 1.0.0 this is the initial stable release
- Update file logo to Torque3D logo

## 0.2.0

### Added

- Support for simple completion of functions

### Fixed

- Parsing of block statements
- Error-handling
