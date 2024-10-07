# Jenkins Maintenance Mode

[![CI](https://github.com/offa/maintenance-mode/workflows/ci/badge.svg)](https://github.com/offa/maintenance-mode/actions)
[![GitHub release](https://img.shields.io/github/release/offa/maintenance-mode.svg)](https://github.com/offa/maintenance-mode/releases)
[![License](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)
![Java](https://img.shields.io/badge/java-17-green.svg)

Stops executing new builds across restarts.

Unlike *Prepare for Shutdown*, the maintenance mode keeps the state until disabled explicitly. 

## Usage

Enable / disable the maintenance mode through ***Manage Jenkins → Maintenance Mode***.