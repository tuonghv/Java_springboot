## CONFIG LOCAL ENV
echo "[*] Config local environment..."
# alias vault='docker-compose exec vault vault "$@"'
export VAULT_ADDR=http://127.0.0.1:8200

## INIT VAULT
echo "[*] Init vault..."
rm -f /vault/_data/keys.txt
vault operator init > /vault/_data/keys.txt
export VAULT_TOKEN=$(grep 'Initial Root Token:' /vault/_data/keys.txt | awk '{print substr($NF, 1, length($NF)-1)}')

## UNSEAL VAULT
echo "[*] Unseal vault..."
vault operator unseal $(grep 'Key 1:' /vault/_data/keys.txt | awk '{print $NF}')
vault operator unseal $(grep 'Key 2:' /vault/_data/keys.txt | awk '{print $NF}')
vault operator unseal $(grep 'Key 3:' /vault/_data/keys.txt | awk '{print $NF}')

## AUTH
echo "[*] Auth..."


## CREATE USER
echo "[*] Create user... Remember to change the defaults!!"
# vault policy write admin /vault/config/admin.hcl
# vault write  auth/userpass/users/webui password=webui policies=admin
