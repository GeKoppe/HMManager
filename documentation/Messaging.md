# Basic concepts of messaging


# Message format
```json
{
  "topic": "TEST",
  "authType": "basic|ticket",
  "credentials": {
    "username": "name",
    "password": "encryptedPassword",
    "ticket": "ticket"
  },
  "message": {
    "property1": "Some message property",
    "property2": "Some other message property"
  }
}
```