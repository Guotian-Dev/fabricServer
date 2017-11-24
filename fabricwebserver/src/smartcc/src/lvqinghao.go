/*
Copyright IBM Corp. 2016 All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package main

import (
	"fmt"
	"strconv"

	"crypto/x509"
	"encoding/base64"

	"encoding/pem"
	"errors"
	"strings"
	"github.com/hyperledger/fabric/bccsp"
	//"github.com/hyperledger/fabric/bccsp/sw"
	//"github.com/hyperledger/fabric-ca/util"
	//"github.com/hyperledger/fabric-ca/lib/csp"
	"github.com/hyperledger/fabric/bccsp/factory"


	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)


func main_test() string {
	//body := "eyJpZCI6InVzZXIxIiwidHlwZSI6InVzZXIiLCJtYXhfZW5yb2xsbWVudHMiOjAsImFmZmlsaWF0aW9uIjoib3JnMS5kZXBhcnRtZW50MSIsImF0dHJzIjpbXX0="
	token := "LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSUI4RENDQVplZ0F3SUJBZ0lVR0Y1WDdHVCsvS1Z4eGlUME9TV1BjanZjSS9vd0NnWUlLb1pJemowRUF3SXcKY3pFTE1Ba0dBMVVFQmhNQ1ZWTXhFekFSQmdOVkJBZ1RDa05oYkdsbWIzSnVhV0V4RmpBVUJnTlZCQWNURFZOaApiaUJHY21GdVkybHpZMjh4R1RBWEJnTlZCQW9URUc5eVp6RXVaWGhoYlhCc1pTNWpiMjB4SERBYUJnTlZCQU1UCkUyTmhMbTl5WnpFdVpYaGhiWEJzWlM1amIyMHdIaGNOTVRjd05qQXhNRGt6TWpBd1doY05NVGd3TkRNd01UY3oKTWpBd1dqQVFNUTR3REFZRFZRUURFd1ZoWkcxcGJqQlpNQk1HQnlxR1NNNDlBZ0VHQ0NxR1NNNDlBd0VIQTBJQQpCRkU5OFQ0b1NSZDNDVWlUaXEyTmdpelVqajdXK0U5dzI0R1k1b2p5MjZwL3JMMW1HRkhhczU0RXQ1VzVZU2Y5CjJnd1l3RnhyQjJRZUZ6MFIydkZHaVJlamJEQnFNQTRHQTFVZER3RUIvd1FFQXdJQ0JEQU1CZ05WSFJNQkFmOEUKQWpBQU1CMEdBMVVkRGdRV0JCUnZpRkZkUFgwZXc3N0pZWHRwdWM4TGJoam1qVEFyQmdOVkhTTUVKREFpZ0NDNwpzZDlkWktKSGY1VEhHS09FMjJkYzhqckNJMlN4VjZTU3Z5cVZLL0pyNGpBS0JnZ3Foa2pPUFFRREFnTkhBREJFCkFpQkRrOXBocStQWkljQWIxbmtyNzlCWWEra3VyazlMa09WRU9lMnZ0UUJmaVFJZ2NjdFlmSkE3eVA2c0tiaU4Kc3Nnd1NpcGlZL1hDMFBqaUlKbHcxa1Z0MFBzPQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==.MEQCIE1pYQ1Mvj7NoJDpiyfu5Nla6jM+zlLQURpAgHrrkn1uAiA6emy5oHxZqdP0BT/+mkFsdfan5j36IwhJMD1vJyh7kA=="
	fmt.Println(token)
	//fmt.Println("Hello World!\nGod Bless You!");
	//stringByte := []byte("Hello World!\nGod Bless You!");
	//fmt.Println([]byte(stringByte))
    //fmt.Println(string([]byte(stringByte)))
	factory.InitFactories(nil)
	bccsp := GetDefaultBCCSP()
	body2 := `{"id":"user1","type":"user","max_enrollments":0,"affiliation":"org1.department1","attrs":[]}`
	bodyByte := []byte(body2)
	x, err := VerifyToken(bccsp, token, bodyByte);
	if err != nil {
		fmt.Println("error:. #####################")
		return "VerifyToken error"
	}
	fmt.Println(x);
	return body2;
}


func GetDefaultBCCSP() bccsp.BCCSP {
	return factory.GetDefault()
}


// VerifyToken verifies token signed by either ECDSA or RSA and
// returns the associated user ID
func VerifyToken(csp bccsp.BCCSP, token string, body []byte) (*x509.Certificate, error) {

	if csp == nil {
		return nil, errors.New("BCCSP instance is not present")
	}
	x509Cert, b64Cert, b64Sig, err := DecodeToken(token)
	fmt.Println(x509Cert);
	fmt.Println(b64Cert);
	fmt.Println(b64Sig);
	
	if err != nil {
		return nil, err
	}
	sig, err := B64Decode(b64Sig)
	fmt.Println(len(sig))
	
	if err != nil {
		return nil, fmt.Errorf("Invalid base64 encoded signature in token: %s", err)
	}
	b64Body := B64Encode(body)
	sigString := b64Body + "." + b64Cert
	fmt.Println(sigString);
	

	pk2, err := csp.KeyImport(x509Cert, &bccsp.X509PublicKeyImportOpts{Temporary: true})
	if err != nil {
		return nil, fmt.Errorf("Public Key import into BCCSP failed with error : %s", err)
	}
	if pk2 == nil {
		return nil, errors.New("Public Key Cannot be imported into BCCSP")
	}
	//bccsp.X509PublicKeyImportOpts
	//Using default hash algo
	digest, digestError := csp.Hash([]byte(sigString), &bccsp.SHAOpts{})
	if digestError != nil {
		return nil, fmt.Errorf("Message digest failed with error : %s", digestError)
	}
	fmt.Println(len(digest))

	//fmt.Println(&bccsp.SHAOpts{}.Algorithm())
	//fmt.Println( (&bccsp.SHAOpts{}).(type))
	/*
	switch (&bccsp.SHAOpts{}).(type) {
	case *bccsp.SHAOpts:
		fmt.Println(1)
	case *bccsp.SHA256Opts:
		fmt.Println(2)
	case *bccsp.SHA384Opts:
		fmt.Println(3)
	case *bccsp.SHA3_256Opts:
		fmt.Println(4)
	case *bccsp.SHA3_384Opts:
		fmt.Println(5)
	default:
		fmt.Println(0)
	}
	*/
	

	valid, validErr := csp.Verify(pk2, sig, digest, nil)

	if validErr != nil {
		return nil, fmt.Errorf("Token Signature validation failed with error : %s ", validErr)
	}
	if !valid {
		return nil, errors.New("Token Signature Validation failed")
	}

	return x509Cert, nil
}

// B64Encode base64 encodes bytes
func B64Encode(buf []byte) string {
	return base64.StdEncoding.EncodeToString(buf)
}

// B64Decode base64 decodes a string
func B64Decode(str string) (buf []byte, err error) {
	return base64.StdEncoding.DecodeString(str)
}


// DecodeToken extracts an X509 certificate and base64 encoded signature from a token
func DecodeToken(token string) (*x509.Certificate, string, string, error) {
	if token == "" {
		return nil, "", "", errors.New("Invalid token; it is empty")
	}
	parts := strings.Split(token, ".")
	if len(parts) != 2 {
		return nil, "", "", errors.New("Invalid token format; expecting 2 parts separated by '.'")
	}
	b64cert := parts[0]
	certDecoded, err := B64Decode(b64cert)
	if err != nil {
		return nil, "", "", fmt.Errorf("Failed to decode base64 encoded x509 cert: %s", err)
	}
	block, _ := pem.Decode(certDecoded)
	if block == nil {
		return nil, "", "", errors.New("Failed to PEM decode the certificate")
	}
	x509Cert, err := x509.ParseCertificate(block.Bytes)
	if err != nil {
		return nil, "", "", fmt.Errorf("Error in parsing x509 cert given Block Bytes: %s", err)
	}
	return x509Cert, b64cert, parts[1], nil
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


// SimpleChaincode example simple Chaincode implementation
type SimpleChaincode struct {
}

// Init initializes the chaincode state
func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("########### example_cc Init ###########")
	_, args := stub.GetFunctionAndParameters()
	var A, B string    // Entities
	var Aval, Bval int // Asset holdings
	var err error

	if len(args) != 4 {
		return shim.Error("Incorrect number of arguments. Expecting 4")
	}

	// Initialize the chaincode
	A = args[0]
	Aval, err = strconv.Atoi(args[1])
	if err != nil {
		return shim.Error("Expecting integer value for asset holding")
	}
	B = args[2]
	Bval, err = strconv.Atoi(args[3])
	if err != nil {
		return shim.Error("Expecting integer value for asset holding")
	}
	fmt.Printf("Aval = %d, Bval = %d\n", Aval, Bval)

	// Write the state to the ledger
	err = stub.PutState(A, []byte(strconv.Itoa(Aval)))
	if err != nil {
		return shim.Error(err.Error())
	}

	err = stub.PutState(B, []byte(strconv.Itoa(Bval)))
	if err != nil {
		return shim.Error(err.Error())
	}
	
	//////////////////////////////////////////////////////////////////////////
	err = stub.PutState("c", []byte(main_test())) 
	if err != nil {
		return shim.Error(err.Error())
	}
	//////////////////////////////////////////////////////////////////////////

	if transientMap, err := stub.GetTransient(); err == nil {
		if transientData, ok := transientMap["result"]; ok {
			return shim.Success(transientData)
		}
	}
	return shim.Success(nil)

}

// Invoke makes payment of X units from A to B
func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("########### example_cc Invoke ###########")
	function, args := stub.GetFunctionAndParameters()

	if function != "invoke" {
		return shim.Error("Unknown function call")
	}

	if len(args) < 2 {
		return shim.Error("Incorrect number of arguments. Expecting at least 2")
	}

	if args[0] == "delete" {
		// Deletes an entity from its state
		return t.delete(stub, args)
	}

	if args[0] == "query" {
		// queries an entity state
		return t.query(stub, args)
	}
	if args[0] == "move" {
		// Deletes an entity from its state
		return t.move(stub, args)
	}
	
	return shim.Error("Unknown action, check the first argument, must be one of 'delete', 'query', or 'move'")
}

func (t *SimpleChaincode) move(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	// must be an invoke
	var A, B string    // Entities
	var Aval, Bval int // Asset holdings
	var X int          // Transaction value
	var err error

	if len(args) != 4 {
		return shim.Error("Incorrect number of arguments. Expecting 4, function followed by 2 names and 1 value")
	}

	A = args[1]
	B = args[2]

	// Get the state from the ledger
	// TODO: will be nice to have a GetAllState call to ledger
	Avalbytes, err := stub.GetState(A)
	if err != nil {
		return shim.Error("Failed to get state")
	}
	if Avalbytes == nil {
		return shim.Error("Entity not found")
	}
	Aval, _ = strconv.Atoi(string(Avalbytes))

	Bvalbytes, err := stub.GetState(B)
	if err != nil {
		return shim.Error("Failed to get state")
	}
	if Bvalbytes == nil {
		return shim.Error("Entity not found")
	}
	Bval, _ = strconv.Atoi(string(Bvalbytes))

	// Perform the execution
	X, err = strconv.Atoi(args[3])
	if err != nil {
		return shim.Error("Invalid transaction amount, expecting a integer value")
	}
	Aval = Aval - X
	Bval = Bval + X
	fmt.Printf("Aval = %d, Bval = %d\n", Aval, Bval)

	// Write the state back to the ledger
	err = stub.PutState(A, []byte(strconv.Itoa(Aval)))
	if err != nil {
		return shim.Error(err.Error())
	}

	err = stub.PutState(B, []byte(strconv.Itoa(Bval)))
	if err != nil {
		return shim.Error(err.Error())
	}

	if transientMap, err := stub.GetTransient(); err == nil {
		if transientData, ok := transientMap["result"]; ok {
			return shim.Success(transientData)
		}
	}
	return shim.Success(nil)
}

// Deletes an entity from state
func (t *SimpleChaincode) delete(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	A := args[1]

	// Delete the key from the state in ledger
	err := stub.DelState(A)
	if err != nil {
		return shim.Error("Failed to delete state")
	}

	return shim.Success(nil)
}

// Query callback representing the query of a chaincode
func (t *SimpleChaincode) query(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	var A string // Entities
	var err error

	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
	}

	A = args[1]

	// Get the state from the ledger
	Avalbytes, err := stub.GetState(A)
	if err != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + A + "\"}"
		return shim.Error(jsonResp)
	}

	if Avalbytes == nil {
		jsonResp := "{\"Error\":\"Nil amount for " + A + "\"}"
		return shim.Error(jsonResp)
	}

	jsonResp := "{\"Name\":\"" + A + "\",\"Amount\":\"" + string(Avalbytes) + "\"}"
	fmt.Printf("Query Response:%s\n", jsonResp)
	return shim.Success(Avalbytes)
}

func main() {
	err := shim.Start(new(SimpleChaincode))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}
