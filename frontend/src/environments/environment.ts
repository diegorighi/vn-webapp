export const environment = {
  production: false,
  envName: 'dev',
  apiUrl: '',  // Usa proxy do Angular dev server
  graphqlEndpoint: '/graphql',  // Relativo, passa pelo proxy
  enableDebugLogs: true,
  authEnabled: false // DEV: sem autenticacao
};
