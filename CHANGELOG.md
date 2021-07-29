# Changelog

## [Unreleased]
### Added

### Changed

### Deprecated

### Removed

### Fixed
- Parent references in object initializers are now properly resolved and highlighted

### Security
## [1.5.1]
### Added
- Hyperlinks for simple file paths in strings

### Changed
- Classified find usages results on objects
- Context-aware completion for method calls on named objects and when type-analysis succeeds
- Context-aware completion for callbacks

### Deprecated

### Removed

### Fixed
- Fixed a bug with formatter indenting body on new instance expressions
- Find usages for local variables is now working

### Security
## [1.5.0]
### Added
- Experimental formatter support

### Changed
- Improved documentation for quick-navigation

### Deprecated

### Removed

### Fixed
- Fixed an issue with folding in switch statements
- Technical Debt: Renamed brack and paren token types
- Fixed highlighting of `default` keyword

### Security
## [1.4.0]
### Added
- Run To Position in Debugger
- Conditional breakpoint support
- Support for generating and importing engine exports
- Documentation support for builtin functions and classes
- Completion support for builtin functions and classes

### Changed

### Deprecated

### Removed

### Fixed
- Namespaced functions had inversed lookup logic

### Security
## [1.3.1]
### Added
- Go to symbol for Objects and Global Variables

### Changed

### Deprecated

### Removed

### Fixed
- Rename refactoring now works for:
  - Global Variables
  - Function Names
  - Object Names
- Step-by-step debugging wasn't working properly, should be working better now

### Security
## [1.3.0]
### Added
- Debugger support for:
  - Line breakpoints
  - Step-by-step debugging
  - On-hover evaluation of variables
  - Evaluation dialog support
  - Stack frame with function arguments

### Changed

### Deprecated

### Removed

### Fixed

### Security
## [1.2.1]
### Added

### Changed

### Deprecated

### Removed

### Fixed
- Parameters did not pass as variables, fixed with a change to the PSI
- Find usages for functions
### Security
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
### Changed

### Deprecated

### Removed

### Fixed

### Security
## [1.1.0]
### Added
- Go to definition on method calls
- Go to definition on object instances
- Go to definition on global variables
- Syntax highlighting for functions and instances

### Changed

### Deprecated

### Removed

### Fixed
- Improvements to grammars to handle more edge-cases

### Security
## [1.0.0]
### Added
- Plugin icon

### Changed
- Update version to 1.0.0 this is the initial stable release
- Update file logo to Torque3D logo

### Deprecated

### Removed

### Fixed

### Security
## [0.2.0]
### Added
- Support for simple completion of functions

### Changed

### Deprecated

### Removed

### Fixed
- Parsing of block statements
- Error-handling

### Security
