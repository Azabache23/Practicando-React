export interface Cliente {
  id: string
  nombre: string
  ubicacion: string
  nif: string
}

export type ClienteForm = Omit<Cliente, 'id'>
