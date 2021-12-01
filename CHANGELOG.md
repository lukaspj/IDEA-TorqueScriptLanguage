# Changelog

## [Unreleased]
### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security
## [1.6.6]

### Fixed
- Use project root as game root when setting breakpoints
  
  This solves an issue on OSX where the game executable is not in the root folder

## [1.6.5]
### Fixed
- Fixed a race-condition when connecting to debugger via telnet

## [1.6.4]
### Changed
- Removed dependency on `com.intellij.java`

### Fixed
- Improved error handling in Rebuild Exports action

## [1.6.3]
### Fixed
- Breakpoints added after application had started would get the 
  string "null" as a condition if no condition were specified. Now it correctly defaults to "true"

## [1.6.2]
### Fixed
- Global Functions are no longer incorrectly prefixed with :: in auto-completion

## [1.6.1]
### Fixed
- Handle more illegal XML chars in the Rebuild Exports action

## [1.6.0]
### Added
- Auto-show local variables when breakpoint is hit

### Changed
- Specify compatibility range of plugin to be anything from 211

### Fixed
- Parent references in object initializers are now properly resolved and highlighted

## [1.5.1]
### Added
- Hyperlinks for simple file paths in strings

### Changed
- Classified find usages results on objects
- Context-aware completion for method calls on named objects and when type-analysis succeeds
- Context-aware completion for callbacks

### Fixed
- Fixed a bug with formatter indenting body on new instance expressions
- Find usages for local variables is now working

## [1.5.0]
### Added
- Experimental formatter support

### Changed
- Improved documentation for quick-navigation

### Fixed
- Fixed an issue with folding in switch statements
- Technical Debt: Renamed brack and paren token types
- Fixed highlighting of `default` keyword

## [1.4.0]
### Added
- Run To Position in Debugger
- Conditional breakpoint support
- Support for generating and importing engine exports
- Documentation support for builtin functions and classes
- Completion support for builtin functions and classes

### Fixed
- Namespaced functions had inversed lookup logic

## [1.3.1]
### Added
- Go to symbol for Objects and Global Variables

### Fixed
- Rename refactoring now works for:
  - Global Variables
  - Function Names
  - Object Names
- Step-by-step debugging wasn't working properly, should be working better now

## [1.3.0]
### Added
- Debugger support for:
  - Line breakpoints
  - Step-by-step debugging
  - On-hover evaluation of variables
  - Evaluation dialog support
  - Stack frame with function arguments

## [1.2.1]
### Fixed
- Parameters did not pass as variables, fixed with a change to the PSI
- Find usages for functions

## [1.2.0]
### Added
- Basic run configuration
- Completion for
  - Local Variables
  - Global Variables
  - Object names
  - Functions
  - Methods
  - Keywords
- Brace Matchine
- Commenter
- Find usages for
  - Global Variables
  - Global Functions
  - Namespaces Functions
  - Object Names
- Basic code folding for blocks
- Navigate to function

## [1.1.0]
### Added
- Go to definition on method calls
- Go to definition on object instances
- Go to definition on global variables
- Syntax highlighting for functions and instances


### Fixed
- Improvements to grammars to handle more edge-cases

## [1.0.0]
### Added
- Plugin icon

### Changed
- Update version to 1.0.0 this is the initial stable release
- Update file logo to Torque3D logo

## [0.2.0]
### Added
- Support for simple completion of functions

### Fixed
- Parsing of block statements
- Error-handling

