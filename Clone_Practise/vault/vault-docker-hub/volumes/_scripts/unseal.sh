## CONFIG LOCAL ENV
echo "[*] Config local environment..."
# alias vault='docker-compose exec vault vault "$@"'
export VAULT_ADDR=http://127.0.0.1:8200

## UNSEAL VAULT
echo "[*] Unseal vault..."
vault operator unseal $(grep 'Key 1:' /vault/_data/keys.txt | awk '{print $NF}')
vault operator unseal $(grep 'Key 2:' /vault/_data/keys.txt | awk '{print $NF}')
vault operator unseal $(grep 'Key 3:' /vault/_data/keys.txt | awk '{print $NF}')