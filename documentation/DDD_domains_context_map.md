# Context map / domain description

## Functional domains
Naive, simple initial view

### Core Domain: activity
  * time registration
  * activity/task object
  * templating

### Non-core Domain: notes

## Non-functional domain/layers
  * Front-end
  * Data storage                
    ** major technological/vision concern, but not functionaly "core"

# Proposed structure:
**Dependencies from top to bottom**
  * top-level module for infrastructure
    * data storage
    * data representation
    * logging
  * top-level module for business logical
    ** converters, combinations, etc
    ** preprocessors
  * top-level module per domain
    ** Core concepts only => clinically clean
    *** No toString() shizzle in the domain object, as this is a representation concern
  
