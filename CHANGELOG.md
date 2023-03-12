# Changelog
All notable changes to this project will be documented in this file.
    
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- TreeUtils
  - Checks for if block is to be considered natural or if the block is to be growable
    - Growable: i.e. sapling, mushroom, fungus
    - Natural: blocks that aren't naturally spawning but are used in custom tree schematics.
- SaplingListener
  - Actual class to handle growing of custom trees from saplings